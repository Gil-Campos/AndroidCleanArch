package com.example.cleanarchitecture.domain.models.characters

data class Character(
    val id: Long,
    val name: String,
    val status: Boolean,
    val image: String
)
