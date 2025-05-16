package com.example.cleanarchitecture.data.service

import com.example.cleanarchitecture.data.model.CharactersResponse
import retrofit2.Response
import retrofit2.http.GET


interface CharactersApi {
    @GET("/api/character")
    suspend fun getCharacters(): Response<CharactersResponse>
}