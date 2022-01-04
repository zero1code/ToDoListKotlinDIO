package com.camerax.todolist

import android.app.Application
import com.camerax.todolist.data.di.DataModules
import com.camerax.todolist.domain.di.DomainModule
import com.camerax.todolist.presentation.di.PresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
        }

        DataModules.load()
        DomainModule.load()
        PresentationModule.load()
    }
}