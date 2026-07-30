package com.example.eduapp.game

/**
 * A single picture-math puzzle.
 *
 * @param imagePath path relative to assets/, e.g. "1/level01_pic01_0.png"
 * @param answer the correct integer value for "?"
 */
data class Puzzle(
    val imagePath: String,
    val answer: Int
)
