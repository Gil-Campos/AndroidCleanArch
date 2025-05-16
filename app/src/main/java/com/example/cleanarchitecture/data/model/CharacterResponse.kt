package com.example.cleanarchitecture.data.model

data class CharacterResponse(
    val id: Long? = null,
    val name: String? = null,
    val status: CharacterStatusResponse? = null,
    val image: String? = null
)