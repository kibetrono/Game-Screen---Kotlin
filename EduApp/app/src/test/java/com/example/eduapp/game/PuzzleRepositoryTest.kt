package com.example.eduapp.game

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PuzzleRepositoryTest {

    @Test
    fun `every level from MIN to MAX has exactly 6 puzzles`() {
        for (level in PuzzleRepository.MIN_LEVEL..PuzzleRepository.MAX_LEVEL) {
            val puzzles = PuzzleRepository.getLevel(level)
            assertEquals("Level $level should have 6 puzzles", 6, puzzles.size)
        }
    }

    @Test
    fun `every puzzle has a non-blank image path`() {
        for (level in PuzzleRepository.MIN_LEVEL..PuzzleRepository.MAX_LEVEL) {
            PuzzleRepository.getLevel(level).forEach { puzzle ->
                assertTrue(
                    "Image path should not be blank for level $level",
                    puzzle.imagePath.isNotBlank()
                )
            }
        }
    }

    @Test
    fun `image paths start with the matching level folder`() {
        for (level in PuzzleRepository.MIN_LEVEL..PuzzleRepository.MAX_LEVEL) {
            PuzzleRepository.getLevel(level).forEach { puzzle ->
                assertTrue(
                    "Puzzle image '${puzzle.imagePath}' should live under folder '$level/'",
                    puzzle.imagePath.startsWith("$level/")
                )
            }
        }
    }

    @Test
    fun `out-of-range level falls back to level 1`() {
        assertEquals(PuzzleRepository.getLevel(1), PuzzleRepository.getLevel(999))
    }
}
