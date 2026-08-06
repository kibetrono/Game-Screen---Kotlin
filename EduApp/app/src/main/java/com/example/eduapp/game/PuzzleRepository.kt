package com.example.eduapp.game

/**
 * Central source of truth for every level's puzzles.
 *
 * Answers were worked out by solving each picture-equation algebraically.
 * If you swap in different images for a level, update the matching answer here.
 */
object PuzzleRepository {

    private val level1 = listOf(
        Puzzle("1/level01_pic01_0.png", 0),   // 5 + carrot = 5  -> carrot = 0
        Puzzle("1/level01_pic02_21.png", 25), // apple=7, grape=12, banana=6 -> 25
        Puzzle("1/level01_pic03_15.png", 20), // 3,6,12 / 4,8,16 / 5,10,20
        Puzzle("1/level01_pic04_55.jpg", 55), // black=15, blue=10, red=4 -> 15+10x4
        Puzzle("1/level01_pic05_6.jpg", 6),   // pelican=3, sheep=2 -> 3x2
        Puzzle("1/level01_pic06_0.png", 0)    // 4 cats + cheese = 3 cats + cat -> cheese = 0
    )

    private val level2 = listOf(
        Puzzle("2/level02_pic01_31.jpg", 31), // cat=10, pig=16, horse=5 -> 31
        Puzzle("2/level02_pic02_26.jpg", 26), // grape=12, strawberry=12, watermelon=2 -> 26
        Puzzle("2/level02_pic03_4.jpg", 4),   // solving the 4-character system -> 4
        Puzzle("2/level02_pic04_2.jpg", 2),   // giraffe=8, tiger=2 -> 8 - 2x3 = 2
        Puzzle("2/level02_pic05_35.jpg", 55), // banana=15, cherry=10, apple=4 -> 15+10x4
        Puzzle("2/level02_pic06_63.jpg", 63)  // cheese=5, taco=9, rice=7 -> 7x9
    )

    private val level3 = listOf(
        Puzzle("3/level03_pic01_27.jpg", 27), // cat=7, rabbit=3, dog=17 -> 27
        Puzzle("3/level03_pic02_4.jpg", 4),   // girl=3, boy=-1 -> girl-boy=4
        Puzzle("3/level03_pic03_5.jpg", 8),   // dog=2, giraffe=6 -> 8
        Puzzle("3/level03_pic04_24.jpg", 24), // batter=12, pitcher=1, catcher=0 -> 24
        Puzzle("3/level03_pic05_25.jpg", 25), // jeep=car=scooter=8, bike=1 -> 25
        Puzzle("3/level03_pic06_4.jpg", 4)    // red x blue x green = 288 -> green=4
    )

    private val level4 = listOf(
        Puzzle("4/level04_pic01_15.png", 4),  // star=8, moon=4
        Puzzle("4/level04_pic02_4.png", 6),   // umbrella=8, cloud=6
        Puzzle("4/level04_pic03_36.png", 8),  // bell=4, drum=8
        Puzzle("4/level04_pic04_32.png", 4),  // robot=6, gear=4
        Puzzle("4/level04_pic05_29.png", 4),  // fish=6, shell=4
        Puzzle("4/level04_pic06_18.png", 3)   // kite=5, balloon=3
    )

    private val level5 = listOf(
        Puzzle("5/level05_pic01_14.png", 6),  // diamond=6, ring=6
        Puzzle("5/level05_pic02_70.png", 12), // star=4, rocket=12
        Puzzle("5/level05_pic03_12.png", 6),  // lion=10, zebra=6
        Puzzle("5/level05_pic04_55.png", 20), // clock=5, bell=10, bell x2=20
        Puzzle("5/level05_pic05_5.png", 6),   // cup=6, plate=10
        Puzzle("5/level05_pic06_4.png", 7)    // puzzle piece=9, box=7
    )

    /** Returns the 6 puzzles for the given level (1-5). Falls back to level 1 if out of range. */
    fun getLevel(level: Int): List<Puzzle> = when (level) {
        1 -> level1
        2 -> level2
        3 -> level3
        4 -> level4
        5 -> level5
        else -> level1
    }

    const val MIN_LEVEL = 1
    const val MAX_LEVEL = 5
}
