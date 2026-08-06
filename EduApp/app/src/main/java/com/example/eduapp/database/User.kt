package com.example.eduapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A single saved game result: one row per completed run of a level.
 * Despite the name, this isn't an account/profile - "username" is just the
 * name the player typed in on the Setting screen before that particular run.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val level: String = "1",
    val score: Int = 0,
    val duration: Int = 0,
    val date: Long = System.currentTimeMillis() // default current timestamp
)
