package com.example.eduapp.network

import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class FirestoreStringValue(val stringValue: String)
data class FirestoreIntValue(val integerValue: String)

data class FirestoreResultFields(
    val fields: Map<String, Any>
)

/**
 * Minimal Firestore REST client used for the optional Cloud DB sync feature.
 * See CloudConfig for how to enable this.
 */
interface CloudDbService {
    @POST("v1/projects/{projectId}/databases/(default)/documents/results")
    suspend fun uploadResult(
        @Path("projectId") projectId: String,
        @Query("key") apiKey: String,
        @Body body: FirestoreResultFields
    ): ResponseBody
}
