package com.example.eduapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eduapp.helper.SoundManager
import com.example.eduapp.helper.rememberAssetImage
import com.example.eduapp.viewmodel.AppViewModel
import com.example.eduapp.viewmodel.GameSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    navController: NavHostController,
    session: GameSessionViewModel,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val puzzle = session.currentPuzzle
    val soundEnabled by appViewModel.soundEnabled.collectAsState(initial = true)

    // Sound feature: one SoundManager per time this screen is on-screen, released
    // when the screen leaves composition so we never leak the underlying tone player.
    val soundManager = remember { SoundManager() }
    DisposableEffect(Unit) {
        onDispose { soundManager.release() }
    }

    var answerText by rememberSaveable(session.currentPuzzleIndex) { mutableStateOf("") }
    var feedback by rememberSaveable(session.currentPuzzleIndex) { mutableStateOf<Boolean?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Game Screen") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (puzzle == null) {
                Text("No puzzles found for this level.")
                return@Column
            }

            Text(
                text = "Level ${session.selectedLevel} · Puzzle ${session.currentPuzzleIndex + 1} of ${session.totalPuzzles}",
                style = MaterialTheme.typography.titleMedium
            )
            LinearProgressIndicator(
                progress = { (session.currentPuzzleIndex + 1) / session.totalPuzzles.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            Text(
                text = "Score: ${session.score}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            val imageBitmap = rememberAssetImage(puzzle.imagePath)
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Puzzle image",
                    modifier = Modifier.size(300.dp)
                )
            } else {
                Text(
                    text = "Error: could not load image at ${puzzle.imagePath}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = answerText,
                onValueChange = { input ->
                    // Input validation: only allow an optional leading minus sign
                    // followed by digits, so the field can never hold garbage input.
                    if (input.isEmpty() || input.matches(Regex("-?\\d*"))) {
                        answerText = input
                    }
                },
                label = { Text("Your answer for '?'") },
                singleLine = true,
                isError = feedback == false,
                enabled = feedback == null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (feedback) {
                true -> Text("Correct! 🎉", color = MaterialTheme.colorScheme.primary)
                false -> Text(
                    "Not quite. The answer was ${puzzle.answer}.",
                    color = MaterialTheme.colorScheme.error
                )
                null -> {}
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (feedback == null) {
                Button(
                    onClick = {
                        val parsed = answerText.trim().toIntOrNull()
                        val correct = session.submitAnswer(parsed ?: Int.MIN_VALUE)
                        feedback = correct
                        if (soundEnabled) {
                            if (correct) soundManager.playCorrect() else soundManager.playIncorrect()
                        }
                    },
                    enabled = answerText.isNotBlank()
                ) {
                    Text("Submit")
                }
            } else {
                val wasLast = session.isLastPuzzle
                Button(
                    onClick = {
                        val hasMore = session.advance()
                        if (!hasMore) {
                            if (soundEnabled) soundManager.playLevelComplete()
                            navController.navigate("score") {
                                popUpTo("game") { inclusive = true }
                            }
                        }
                    }
                ) {
                    Text(if (wasLast) "Finish" else "Next")
                }
            }
        }
    }
}
