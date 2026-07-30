@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.eduapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eduapp.screen.GameScreen
import com.example.eduapp.screen.HistoryScreen
import com.example.eduapp.screen.LandingScreen
import com.example.eduapp.screen.ScoreScreen
import com.example.eduapp.screen.SettingScreen
import com.example.eduapp.ui.theme.EduAppTheme
import com.example.eduapp.viewmodel.AppViewModel
import com.example.eduapp.viewmodel.AppViewModelFactory
import com.example.eduapp.viewmodel.GameSessionViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EduAppTheme {
                AppNav()
            }
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()

    // App Architecture / DI: every dependency (DB, network services, preferences)
    // is built once by EduApplication's AppContainer, not constructed here. The
    // ViewModels below are created once and shared by every screen in the graph.
    val context = LocalContext.current
    val container = remember { (context.applicationContext as EduApplication).container }
    val factory = remember { AppViewModelFactory(container) }
    val appViewModel: AppViewModel = viewModel(factory = factory)
    val session: GameSessionViewModel = viewModel()

    NavHost(navController = navController, startDestination = "landing") {
        composable("landing") { LandingScreen(navController) }
        composable("setting") { SettingScreen(navController, session, appViewModel) }
        composable("game") { GameScreen(navController, session, appViewModel) }
        composable("score") { ScoreScreen(navController, session, appViewModel) }
        composable("history") { HistoryScreen(navController, appViewModel) }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EduAppTheme {

    }
}
