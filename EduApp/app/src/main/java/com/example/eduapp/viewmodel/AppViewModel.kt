package com.example.eduapp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eduapp.data.UserPreferencesRepository
import com.example.eduapp.database.AppDao
import com.example.eduapp.database.User
import com.example.eduapp.network.CloudSyncRepository
import com.example.eduapp.network.NumbersApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Handles everything about saved results: reading history, saving a new result
 * (locally in Room, and optionally mirrored to the cloud), editing/deleting entries,
 * fetching a fun Web API fact about the player's score, and reading/writing the
 * small sound + last-username preferences.
 */
class AppViewModel(
    private val dao: AppDao,
    private val numbersApiService: NumbersApiService,
    private val cloudSyncRepository: CloudSyncRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val users: Flow<List<User>> = dao.getAllUsers()

    val soundEnabled: Flow<Boolean> = preferencesRepository.soundEnabled
    val lastUsername: Flow<String> = preferencesRepository.lastUsername

    /** Web API feature: a short fun fact about a number, shown on the Score screen. */
    var scoreTrivia by mutableStateOf<String?>(null)
        private set
    var scoreTriviaLoading by mutableStateOf(false)
        private set

    /**
     * Saves a completed game result locally, then best-effort mirrors it to the
     * cloud (silently skipped/ignored if cloud sync isn't configured or fails -
     * see CloudSyncRepository).
     */
    fun addResult(username: String, level: Int, score: Int, durationSeconds: Int) {
        viewModelScope.launch {
            val user = User(
                username = username,
                level = level.toString(),
                score = score,
                duration = durationSeconds
            )
            dao.insert(user)
            preferencesRepository.setLastUsername(username)
            cloudSyncRepository.uploadResult(user)
        }
    }

    fun updateUsername(user: User, newUsername: String) {
        if (newUsername.isBlank()) return
        viewModelScope.launch {
            dao.update(user.copy(username = newUsername.trim()))
        }
    }

    fun deleteResult(user: User) {
        viewModelScope.launch {
            dao.delete(user)
        }
    }

    fun clearUsers() {
        viewModelScope.launch {
            dao.deleteAll()
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setSoundEnabled(enabled)
        }
    }

    /** Fetches a trivia fact about [number] (typically the player's score) from the Web API. */
    fun fetchScoreTrivia(number: Int) {
        viewModelScope.launch {
            scoreTriviaLoading = true
            scoreTrivia = try {
                numbersApiService.getFact(number).text
            } catch (e: Exception) {
                null // Offline or API unavailable - Score screen just hides the trivia line.
            }
            scoreTriviaLoading = false
        }
    }
}
