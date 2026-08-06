package com.example.eduapp.network

/**
 * Cloud DB (Firestore) configuration.
 *
 * This app's primary data store is the local Room database - it works fully offline
 * with no configuration needed. Cloud sync is an *optional* extra: if you want game
 * results also mirrored to a Firebase Firestore database, create a Firebase project,
 * enable Firestore, and fill in the two values below (Project ID + a Web API key with
 * Firestore access). Leave PROJECT_ID blank to keep cloud sync disabled - the app
 * checks this before ever making a network call, so nothing breaks if it's unset.
 */
object CloudConfig {
    const val PROJECT_ID: String = ""   // e.g. "eduapp-12345"
    const val API_KEY: String = ""      // Firebase Web API key with Firestore access

    val isConfigured: Boolean
        get() = PROJECT_ID.isNotBlank() && API_KEY.isNotBlank()
}
