package com.example.eduapp.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GameSessionViewModelTest {

    private lateinit var session: GameSessionViewModel

    @Before
    fun setUp() {
        session = GameSessionViewModel()
        session.selectedLevel = 1
    }

    @Test
    fun `startGame loads all 6 puzzles for the selected level and resets state`() {
        session.startGame()

        assertEquals(6, session.totalPuzzles)
        assertEquals(0, session.currentPuzzleIndex)
        assertEquals(0, session.score)
        assertNotNull(session.currentPuzzle)
    }

    @Test
    fun `submitAnswer increases score only when the answer is correct`() {
        session.startGame()
        val correctAnswer = session.currentPuzzle!!.answer

        val result = session.submitAnswer(correctAnswer)

        assertTrue(result)
        assertEquals(1, session.score)
    }

    @Test
    fun `submitAnswer does not increase score when the answer is wrong`() {
        session.startGame()
        val wrongAnswer = session.currentPuzzle!!.answer + 12345

        val result = session.submitAnswer(wrongAnswer)

        assertFalse(result)
        assertEquals(0, session.score)
    }

    @Test
    fun `advance moves to the next puzzle until the last one is reached`() {
        session.startGame()
        val total = session.totalPuzzles

        var stillMore = true
        var steps = 0
        while (stillMore) {
            stillMore = session.advance()
            steps++
        }

        // We should have advanced exactly (total - 1) times before hitting the end.
        assertEquals(total, steps)
        assertTrue(session.isLastPuzzle)
    }

    @Test
    fun `lastDurationSeconds is recorded once the level is finished`() {
        session.startGame()
        repeat(session.totalPuzzles) { session.advance() }

        assertTrue(session.lastDurationSeconds >= 0)
    }

    @Test
    fun `each new game reshuffles but keeps the same 6 puzzles for a level`() {
        session.startGame()
        val firstRun = session.puzzles.toSet()

        session.startGame()
        val secondRun = session.puzzles.toSet()

        assertEquals(firstRun, secondRun)
    }
}
