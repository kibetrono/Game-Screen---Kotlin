package com.example.eduapp

import android.app.Application
import com.example.eduapp.di.AppContainer
import com.example.eduapp.di.DefaultAppContainer

class EduApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
