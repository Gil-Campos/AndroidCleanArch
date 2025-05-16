package com.example.cleanarchitecture.domain.repository

import com.example.cleanarchitecture.domain.models.characters.Characters
import com.example.cleanarchitecture.domain.models.network.NetworkResult
import kotlinx.coroutines.flow.Flow

interface CharactersRepository {
    suspend fun getCharacters(): Flow<NetworkResult<Characters>>
}