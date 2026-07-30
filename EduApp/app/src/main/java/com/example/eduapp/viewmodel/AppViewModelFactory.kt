package com.example.eduapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eduapp.di.AppContainer

/**
 * Builds an AppViewModel wired up with every dependency it needs, all sourced
 * from a single AppContainer (see di/AppContainer.kt).
 */
class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AppViewModel(
            dao = container.appDao,
            numbersApiService = container.numbersApiService,
            cloudSyncRepository = container.cloudSyncRepository,
            preferencesRepository = container.userPreferencesRepository
        ) as T
    }
}
