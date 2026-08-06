package com.example.eduapp.network

import android.util.Log
import com.example.eduapp.database.User

/**
 * Wraps the optional Cloud DB sync so callers never need to worry about whether
 * it's configured, or whether the network call fails
 */
class CloudSyncRepository(private val cloudDbService: CloudDbService) {

    suspend fun uploadResult(user: User) {
        if (!CloudConfig.isConfigured) {
            Log.d(TAG, "Cloud sync skipped: CloudConfig.PROJECT_ID/API_KEY not set")
            return
        }
        try {
            val fields = mapOf(
                "username" to FirestoreStringValue(user.username),
                "level" to FirestoreStringValue(user.level),
                "score" to FirestoreIntValue(user.score.toString()),
                "duration" to FirestoreIntValue(user.duration.toString()),
                "date" to FirestoreIntValue(user.date.toString())
            )
            cloudDbService.uploadResult(
                projectId = CloudConfig.PROJECT_ID,
                apiKey = CloudConfig.API_KEY,
                body = FirestoreResultFields(fields)
            )
            Log.d(TAG, "Cloud sync succeeded for ${user.username}")
        } catch (e: Exception) {
            // Never let a network failure affect local gameplay/score saving.
            Log.w(TAG, "Cloud sync failed, continuing offline", e)
        }
    }

    companion object {
        private const val TAG = "CloudSyncRepository"
    }
}
