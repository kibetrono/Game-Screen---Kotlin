package com.example.eduapp.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eduapp.viewmodel.AppViewModel
import com.example.eduapp.viewmodel.GameSessionViewModel

//Score Screen: shows the result of the run just played, saves it (locally + optional
//cloud sync), and shows a Web API fun-fact about the player's score.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoreScreen(
    navController: NavHostController,
    session: GameSessionViewModel,
    appViewModel: AppViewModel,
    modifier: Modifier = Modifier
) {
    // Save the result exactly once when this screen is first shown for this run,
    // and kick off the Web API trivia fetch for the score.
    var saved by remember { mutableStateOf(false) }
    LaunchedEffect(session.username, session.selectedLevel, session.score, session.lastDurationSeconds) {
        if (!saved) {
            appViewModel.addResult(
                username = session.username,
                level = session.selectedLevel,
                score = session.score,
                durationSeconds = session.lastDurationSeconds
            )
            appViewModel.fetchScoreTrivia(session.score)
            saved = true
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Score Screen") }) }
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Nice work, ${session.username}!",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Level: ${session.selectedLevel}")
            Text("Score: ${session.score} / ${session.totalPuzzles}")
            Text("Time: ${session.lastDurationSeconds}s")

            Spacer(modifier = Modifier.height(16.dp))

            when {
                appViewModel.scoreTriviaLoading -> CircularProgressIndicator(modifier = Modifier.height(24.dp))
                appViewModel.scoreTrivia != null -> Text(
                    text = "Fun fact about ${session.score}: ${appViewModel.scoreTrivia}",
                    style = MaterialTheme.typography.bodyMedium
                )
                else -> {} // Offline or API unavailable - just omit the trivia line.
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = {
                navController.navigate("landing") {
                    popUpTo("landing") { inclusive = true }
                }
            }) {
                Text("Play Again")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(onClick = {
                navController.navigate("history")
            }) {
                Text("View History")
            }
        }
    }
}
