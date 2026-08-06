package com.example.eduapp.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Response shape from numbersapi.com when called with ?json=true, e.g.
 * { "text": "5 is the number of...", "number": 5, "found": true, "type": "trivia" }
 */
data class NumberFactResponse(
    val text: String,
    val number: Int,
    val found: Boolean,
    val type: String
)

/**
 * Web API feature: fetches a fun trivia fact about a number (used to show a fact
 * about the player's score on the Score screen). Free, keyless public API.
 */
interface NumbersApiService {
    @GET("{number}")
    suspend fun getFact(
        @Path("number") number: Int,
        @Query("json") json: Boolean = true
    ): NumberFactResponse
}
