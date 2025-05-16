package com.example.cleanarchitecture.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.cleanarchitecture.domain.models.characters.Character
import com.example.cleanarchitecture.domain.models.network.NetworkResult

@Composable
fun CharacterListScreen(modifier: Modifier = Modifier, viewModel: CharactersViewModel) {

    val stateCharacters by viewModel.characters.collectAsStateWithLifecycle()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (stateCharacters) {
                is NetworkResult.Error -> {
                    val error = (stateCharacters as NetworkResult.Error).message.orEmpty()
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is NetworkResult.Loading -> CircularProgressIndicator(
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )

                is NetworkResult.Success -> {
                    val charactersList =
                        (stateCharacters as NetworkResult.Success).data?.results.orEmpty()

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(charactersList) { character ->
                            CharacterItem(character = character)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterItem(modifier: Modifier = Modifier, character: Character) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = Color.Gray)
    ) {
        Row {
            AsyncImage(
                model = character.image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(150.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.size(16.dp))
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                Text(text = "ID: ${character.id}")
                Spacer(modifier = Modifier.size(8.dp))

                Text(text = "Name: ${character.name}")
                Spacer(modifier = Modifier.size(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Status of character: ")
                    Box(
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(color = if (character.status) Color.Green else Color.Red)
                    )
                }

            }
        }
    }
}