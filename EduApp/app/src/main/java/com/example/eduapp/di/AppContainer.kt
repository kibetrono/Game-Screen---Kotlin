package com.example.eduapp.di

import android.content.Context
import com.example.eduapp.data.DataStoreUserPreferencesRepository
import com.example.eduapp.data.UserPreferencesRepository
import com.example.eduapp.database.AppDao
import com.example.eduapp.database.AppDatabase
import com.example.eduapp.network.CloudDbService
import com.example.eduapp.network.CloudSyncRepository
import com.example.eduapp.network.NumbersApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * App Architecture: dependency injection.
 *
 * This project uses a small manual DI container rather than Hilt/Koin. That's a
 * deliberate choice for a project this size: it keeps every dependency's construction
 * in one obvious place (this file), needs no extra annotation-processing setup on top
 * of the Room/KSP already in use, and is easy to read end-to-end in one sitting.
 * The important architectural property - screens and ViewModels depend on interfaces/
 * constructor parameters, not on concrete classes they build themselves - is the same
 * as you'd get with Hilt.
 */
interface AppContainer {
    val appDao: AppDao
    val numbersApiService: NumbersApiService
    val cloudDbService: CloudDbService
    val cloudSyncRepository: CloudSyncRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val appDao: AppDao by lazy {
        AppDatabase.getInstance(context).appDao()
    }

    private val numbersRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://numbersapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override val numbersApiService: NumbersApiService by lazy {
        numbersRetrofit.create(NumbersApiService::class.java)
    }

    private val firestoreRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://firestore.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    override val cloudDbService: CloudDbService by lazy {
        firestoreRetrofit.create(CloudDbService::class.java)
    }

    override val cloudSyncRepository: CloudSyncRepository by lazy {
        CloudSyncRepository(cloudDbService)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        DataStoreUserPreferencesRepository(context)
    }
}
