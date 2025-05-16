package com.example.cleanarchitecture.data.model

import com.example.cleanarchitecture.domain.models.characters.Character
import com.example.cleanarchitecture.domain.models.characters.Characters

data class CharactersResponse(
    val results: List<CharacterResponse>? = null
) {
    fun toDomain(): Characters {
        return if (results.isNullOrEmpty()) {
            Characters(emptyList())
        } else {
            val characters = results.map {
                Character(
                    id = it.id ?: 0,
                    it.name ?: "",
                    status = it.status?.equals(CharacterStatusResponse.Alive) ?: false,
                    image = it.image ?: ""
                )
            }
            Characters(
                results = characters
            )
        }
    }
}
