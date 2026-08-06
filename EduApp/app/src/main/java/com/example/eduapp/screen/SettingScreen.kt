package com.example.eduapp.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eduapp.game.PuzzleRepository
import com.example.eduapp.viewmodel.AppViewModel
import com.example.eduapp.viewmodel.GameSessionViewModel

private const val MAX_USERNAME_LENGTH = 20

//Setting screen: capture username + chosen level before starting a run
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavHostController,
    session: GameSessionViewModel,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    val lastUsername by appViewModel.lastUsername.collectAsState(initial = "")
    val soundEnabled by appViewModel.soundEnabled.collectAsState(initial = true)
    var prefilled by remember { mutableStateOf(false) }

    // Pre-fill with the last username used (Other/DataStore feature), only once,
    // and only if the player hasn't already typed something in this session.
    LaunchedEffect(lastUsername) {
        if (!prefilled && session.username.isBlank() && lastUsername.isNotBlank()) {
            session.username = lastUsername
        }
        prefilled = true
    }

    // Input validation: blank, whitespace-only, or overly long usernames are rejected.
    val trimmedUsername = session.username.trim()
    val usernameError = when {
        trimmedUsername.isEmpty() -> null // don't show an error before they've typed anything
        trimmedUsername.length > MAX_USERNAME_LENGTH -> "Max $MAX_USERNAME_LENGTH characters"
        else -> null
    }
    val isUsernameValid = trimmedUsername.isNotEmpty() && trimmedUsername.length <= MAX_USERNAME_LENGTH

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setting Screen") },
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
                .padding(16.dp)
        ) {
            Text(
                text = "Enter your name",
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = session.username,
                onValueChange = {
                    if (it.length <= MAX_USERNAME_LENGTH + 10) session.username = it
                },
                label = { Text("Username") },
                singleLine = true,
                isError = usernameError != null,
                supportingText = { if (usernameError != null) Text(usernameError) },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Choose a level",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (level in PuzzleRepository.MIN_LEVEL..PuzzleRepository.MAX_LEVEL) {
                    FilterChip(
                        selected = session.selectedLevel == level,
                        onClick = { session.selectedLevel = level },
                        label = { Text("Level $level") }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Sound effects",
                    style = MaterialTheme.typography.titleMedium
                )
                Switch(
                    checked = soundEnabled,
                    onCheckedChange = { appViewModel.setSoundEnabled(it) }
                )
            }

            Button(
                onClick = {
                    session.username = trimmedUsername
                    session.startGame()
                    navController.navigate("game")
                },
                enabled = isUsernameValid,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                Text("Play Game")
            }
        }
    }
}