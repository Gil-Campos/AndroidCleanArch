package com.example.cleanarchitecture.domain.use_case

import com.example.cleanarchitecture.domain.models.characters.Characters
import com.example.cleanarchitecture.domain.models.network.NetworkResult
import com.example.cleanarchitecture.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCharactersUseCase @Inject constructor(private val repository: CharactersRepository) {
    suspend operator fun invoke(): Flow<NetworkResult<Characters>> {
        return repository.getCharacters()
    }
}