package com.example.eduapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.eduapp.game.Puzzle
import com.example.eduapp.game.PuzzleRepository

/**
 * Holds the state of the player's current run: who they are, which level they picked,
 * how far through the puzzle set they are, their score, and how long they've taken.
 *
 * This single instance is created once (in AppNav) and shared by SettingScreen,
 * GameScreen and ScoreScreen so results actually flow between screens instead of
 * being lost on navigation.
 */
class GameSessionViewModel : ViewModel() {

    var username by mutableStateOf("")
    var selectedLevel by mutableIntStateOf(1)

    var currentPuzzleIndex by mutableIntStateOf(0)
    var score by mutableIntStateOf(0)

    private var startTimeMillis: Long = 0L
    var lastDurationSeconds by mutableIntStateOf(0)

    var puzzles: List<Puzzle> = emptyList()
        private set

    val totalPuzzles: Int get() = puzzles.size
    val currentPuzzle: Puzzle? get() = puzzles.getOrNull(currentPuzzleIndex)
    val isLastPuzzle: Boolean get() = currentPuzzleIndex >= puzzles.size - 1

    /** Call when the player presses "Start Game" on the Setting screen. */
    fun startGame() {
        // App functionality: random - puzzles are shuffled so replaying a level
        // doesn't show the same 6 puzzles in the same order every time.
        puzzles = PuzzleRepository.getLevel(selectedLevel).shuffled()
        currentPuzzleIndex = 0
        score = 0
        startTimeMillis = System.currentTimeMillis()
    }

    /** Call after the player submits an answer for the current puzzle. Returns whether it was correct. */
    fun submitAnswer(answer: Int): Boolean {
        val correct = currentPuzzle?.answer == answer
        if (correct) score += 1
        return correct
    }

    /** Advances to the next puzzle. Returns false if there is no next puzzle (level finished). */
    fun advance(): Boolean {
        if (isLastPuzzle) {
            lastDurationSeconds = ((System.currentTimeMillis() - startTimeMillis) / 1000).toInt()
            return false
        }
        currentPuzzleIndex += 1
        return true
    }
}
