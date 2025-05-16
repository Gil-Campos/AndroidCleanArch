package com.example.cleanarchitecture.data.repository

import com.example.cleanarchitecture.data.service.CharactersApi
import com.example.cleanarchitecture.domain.models.characters.Characters
import com.example.cleanarchitecture.domain.models.network.NetworkResult
import com.example.cleanarchitecture.domain.repository.CharactersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CharactersRepositoryImpl @Inject constructor(private val api: CharactersApi) :
    CharactersRepository {
    override suspend fun getCharacters(): Flow<NetworkResult<Characters>> = flow {
        try {
            val response = api.getCharacters()

            if (response.isSuccessful) {
                response.body()?.let { emit(NetworkResult.Success(data = it.toDomain())) }
            } else {
                emit(NetworkResult.Error(message = "Error occurred: ${response.code()}"))
            }

        } catch (e: Exception) {
            emit(NetworkResult.Error(message = e.message))
        }
    }
}