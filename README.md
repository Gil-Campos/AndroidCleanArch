## 🏗️ Clean Architecture Deep Dive

Clean Architecture splits your code into **three main layers**, each with a single responsibility. By drawing clear boundaries, you ensure **maintainability**, **testability**, and **scalability**.

---

### 📦 1. Data Layer (“Suppliers & Warehouses”)

**Responsibilities**  
- Fetch raw data from external sources (network, cache, disk).  
- Map protocol/DB formats into intermediate DTOs.  
- Expose a reactive stream (`Flow<Result<…>>`) of **domain models** via repository implementations.

**Why this way?**  
- Keeps network or database details (Retrofit, Room) **out of** business logic and UI.  
- Centralizes error handling and caching logic in one place.  

**Example**  
```kotlin
// DTO from API
data class PostDto(val id: Int, val title: String, val body: String)

// Retrofit interface
interface PostApiService {
  @GET("posts") suspend fun fetchPosts(): List<PostDto>
}

// RepositoryImpl: maps DTO → Domain, wraps in Flow<Result>
class PostRepositoryImpl(
  private val api: PostApiService
): PostRepository {
  override fun getPosts(): Flow<Result<List<Post>>> = flow {
    emit(Result.Loading)
    val dtos = api.fetchPosts()               // raw JSON → list of PostDto
    val domain = dtos.map { it.toDomain() }   // map to clean domain models
    emit(Result.Success(domain))
  }.catch { e ->
    emit(Result.Error(e.localizedMessage ?: "Unknown"))
  }
}
```

---

### 🍳 2. Domain Layer (“Head Chef’s Kitchen”)

**Responsibilities**

* Define **pure** business models and logic—no Android, no Retrofit or Room imports.
* Declare **repository interfaces**.
* Implement **use-cases** (interactors) that orchestrate repositories, apply business rules, and return `Flow<T>` or simple results.

**Why this way?**

* Pure Kotlin → trivial to unit-test.
* Interfaces allow swapping implementations (e.g., mock repos for tests, new data sources).

**Example**

```kotlin
// domain/model/Post.kt
data class Post(val id: Int, val title: String, val body: String)

// domain/repository/PostRepository.kt
interface PostRepository {
  fun getPosts(): Flow<Result<List<Post>>>
}

// domain/usecase/GetPostsUseCase.kt
class GetPostsUseCase(private val repo: PostRepository) {
  operator fun invoke(): Flow<Result<List<Post>>> =
    repo.getPosts()
}
```

💡 **Inter-layer link:**

* `PostRepositoryImpl` (Data) implements `PostRepository` (Domain).
* Use-case doesn’t know how data is fetched—just calls `repo.getPosts()`.

---

### 🥂 3. Presentation Layer (“Waiter & Dining Room”)

**Responsibilities**

* **ViewModels**:

  * Inject use-cases via Hilt.
  * Collect their Flows into a `MutableStateFlow`/`LiveData`.
  * Expose **immutable** state streams for the UI.
* **Jetpack Compose screens**:

  * Subscribe to ViewModel state with `collectAsStateWithLifecycle()`.
  * Render Loading / Success / Error views.
  * Fire UI events back to ViewModel (e.g., retry button).

**Why this way?**

* One-way data flow: UI ← ViewModel ← Use-case ← Repository.
* Lifecycle-aware state collection prevents leaks.
* Compose’s declarative UI naturally reacts to state changes.

**Example**

```kotlin
@HiltViewModel
class PostsViewModel @Inject constructor(
  private val getPosts: GetPostsUseCase
) : ViewModel() {
  private val _state = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
  val state: StateFlow<UiState<List<Post>>> = _state

  init {
    getPosts()
      .onEach { _state.value = it.toUiState() }
      .launchIn(viewModelScope)
  }
}

@Composable
fun PostsScreen(vm: PostsViewModel = hiltViewModel()) {
  val uiState by vm.state.collectAsStateWithLifecycle()
  when (uiState) {
    is UiState.Loading -> CircularProgressIndicator()
    is UiState.Success -> {
      val posts = (uiState as UiState.Success).data
      LazyColumn { items(posts) { PostItem(it) } }
    }
    is UiState.Error -> Text((uiState as UiState.Error).message)
  }
}
```

💡 **Inter-layer link:**

* `GetPostsUseCase` (Domain) returns the same `Result<List<Post>>` that `PostRepositoryImpl` (Data) emitted—UI just displays it.
* Hilt modules wire `PostRepositoryImpl → PostRepository` and `GetPostsUseCase` into `PostsViewModel`.

---

## 🔗 Dependency Injection (Dagger Hilt)

* **@Module** binds Retrofit, repositories, and use-cases into the DI graph.
* **@HiltViewModel** handles ViewModel creation with injected dependencies.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
  @Provides @Singleton
  fun provideRetrofit(): Retrofit = /* … */

  @Provides @Singleton
  fun provideApi(retrofit: Retrofit): PostApiService =
    retrofit.create(PostApiService::class.java)

  @Provides @Singleton
  fun provideRepo(api: PostApiService): PostRepository =
    PostRepositoryImpl(api)

  @Provides
  fun provideGetPosts(repo: PostRepository) =
    GetPostsUseCase(repo)
}
```

---

### 📊 Why It All Fits Together

1. **Decoupling**: Changing network or DB logic in Data layer never touches Domain or Presentation.
2. **Testable**: Swap in-memory or fake repos for fast unit tests of use-cases and ViewModels.
3. **Modular Growth**: Add new features by creating new DTOs → domain models → use-cases → UI screens, with minimal cross-impact.
4. **Reactive & Safe**: Kotlin Flow + Compose = real-time updates, lifecycle-aware, no manual observers.

> **Final analogy**:
> You’re running a five-star kitchen:
>
> * **Suppliers** (Data) bring in the finest ingredients,
> * Your **Head Chef** (Domain) crafts exquisite dishes,
> * The **Waiter** (Presentation) delivers them to hungry customers (Compose UI).
>   With Hilt as your maître d’ and Flow as the fresh stream of order updates, every part of the restaurant stays in harmony.