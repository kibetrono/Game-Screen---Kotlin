package com.example.eduapp.viewmodel

import com.example.eduapp.data.UserPreferencesRepository
import com.example.eduapp.database.AppDao
import com.example.eduapp.database.User
import com.example.eduapp.network.CloudDbService
import com.example.eduapp.network.CloudSyncRepository
import com.example.eduapp.network.FirestoreResultFields
import com.example.eduapp.network.NumberFactResponse
import com.example.eduapp.network.NumbersApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/** Simple in-memory fake standing in for the real Room DAO. */
private class FakeAppDao : AppDao {
    private val state = MutableStateFlow<List<User>>(emptyList())
    private var nextId = 1

    override suspend fun insert(user: User) {
        state.value = state.value + user.copy(id = nextId++)
    }

    override suspend fun update(user: User) {
        state.value = state.value.map { if (it.id == user.id) user else it }
    }

    override suspend fun delete(user: User) {
        state.value = state.value.filterNot { it.id == user.id }
    }

    override fun getAllUsers(): Flow<List<User>> = state

    override suspend fun deleteAll() {
        state.value = emptyList()
    }
}

/** Fake preferences repo - avoids needing a real Android Context/DataStore in unit tests. */
private class FakeUserPreferencesRepository : UserPreferencesRepository {
    private val sound = MutableStateFlow(true)
    private val username = MutableStateFlow("")

    override val soundEnabled: Flow<Boolean> = sound
    override val lastUsername: Flow<String> = username

    override suspend fun setSoundEnabled(enabled: Boolean) {
        sound.value = enabled
    }

    override suspend fun setLastUsername(username: String) {
        this.username.value = username
    }
}

/** Fake Web API - returns a canned fact, or can be told to fail. */
private class FakeNumbersApiService(private val shouldFail: Boolean = false) : NumbersApiService {
    override suspend fun getFact(number: Int, json: Boolean): NumberFactResponse {
        if (shouldFail) throw java.io.IOException("offline")
        return NumberFactResponse(text = "$number is a great score.", number = number, found = true, type = "trivia")
    }
}

/** Cloud sync is disabled by default (CloudConfig.isConfigured == false), so this is
 *  never actually invoked in these tests - it exists only to satisfy the constructor. */
private class UnusedCloudDbService : CloudDbService {
    override suspend fun uploadResult(projectId: String, apiKey: String, body: FirestoreResultFields): ResponseBody {
        error("Should not be called: CloudConfig is left unconfigured in tests")
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: FakeAppDao
    private lateinit var preferences: FakeUserPreferencesRepository
    private lateinit var viewModel: AppViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        dao = FakeAppDao()
        preferences = FakeUserPreferencesRepository()
        viewModel = AppViewModel(
            dao = dao,
            numbersApiService = FakeNumbersApiService(),
            cloudSyncRepository = CloudSyncRepository(UnusedCloudDbService()),
            preferencesRepository = preferences
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addResult inserts a user and remembers the username`() = runTest {
        viewModel.addResult(username = "Alex", level = 2, score = 5, durationSeconds = 42)
        dispatcher.scheduler.advanceUntilIdle()

        val saved = dao.getAllUsers().first()
        assertEquals(1, saved.size)
        assertEquals("Alex", saved.first().username)
        assertEquals("2", saved.first().level)
        assertEquals(5, saved.first().score)
        assertEquals(42, saved.first().duration)
        assertEquals("Alex", preferences.lastUsername.first())
    }

    @Test
    fun `deleteResult removes only the targeted entry`() = runTest {
        viewModel.addResult("Alex", 1, 3, 10)
        viewModel.addResult("Sam", 2, 4, 20)
        dispatcher.scheduler.advanceUntilIdle()

        val toDelete = dao.getAllUsers().first().first { it.username == "Alex" }
        viewModel.deleteResult(toDelete)
        dispatcher.scheduler.advanceUntilIdle()

        val remaining = dao.getAllUsers().first()
        assertEquals(1, remaining.size)
        assertEquals("Sam", remaining.first().username)
    }

    @Test
    fun `updateUsername edits without deleting the entry`() = runTest {
        viewModel.addResult("Alx", 1, 3, 10)
        dispatcher.scheduler.advanceUntilIdle()

        val entry = dao.getAllUsers().first().first()
        viewModel.updateUsername(entry, "Alex")
        dispatcher.scheduler.advanceUntilIdle()

        val updated = dao.getAllUsers().first()
        assertEquals(1, updated.size)
        assertEquals("Alex", updated.first().username)
    }

    @Test
    fun `updateUsername ignores blank input`() = runTest {
        viewModel.addResult("Alex", 1, 3, 10)
        dispatcher.scheduler.advanceUntilIdle()
        val entry = dao.getAllUsers().first().first()

        viewModel.updateUsername(entry, "   ")
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("Alex", dao.getAllUsers().first().first().username)
    }

    @Test
    fun `clearUsers removes every entry`() = runTest {
        viewModel.addResult("Alex", 1, 3, 10)
        viewModel.addResult("Sam", 2, 4, 20)
        dispatcher.scheduler.advanceUntilIdle()

        viewModel.clearUsers()
        dispatcher.scheduler.advanceUntilIdle()

        assertTrue(dao.getAllUsers().first().isEmpty())
    }

    @Test
    fun `fetchScoreTrivia populates scoreTrivia on success`() = runTest {
        viewModel.fetchScoreTrivia(6)
        dispatcher.scheduler.advanceUntilIdle()

        assertEquals("6 is a great score.", viewModel.scoreTrivia)
        assertTrue(!viewModel.scoreTriviaLoading)
    }

    @Test
    fun `fetchScoreTrivia leaves trivia null when the API call fails`() = runTest {
        val failingViewModel = AppViewModel(
            dao = dao,
            numbersApiService = FakeNumbersApiService(shouldFail = true),
            cloudSyncRepository = CloudSyncRepository(UnusedCloudDbService()),
            preferencesRepository = preferences
        )

        failingViewModel.fetchScoreTrivia(6)
        dispatcher.scheduler.advanceUntilIdle()

        assertNull(failingViewModel.scoreTrivia)
        assertTrue(!failingViewModel.scoreTriviaLoading)
    }
}
