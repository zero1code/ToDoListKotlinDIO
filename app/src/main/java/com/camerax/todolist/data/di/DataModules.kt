package com.camerax.todolist.data.di

import com.camerax.todolist.data.database.AppDatabase
import com.camerax.todolist.data.repository.TaskRepository
import com.camerax.todolist.data.repository.TaskRespositoryImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object DataModules {

    fun load() {
        loadKoinModules(databaseModule() + repositoryModule())
    }

    private fun repositoryModule(): Module {
        return module {
            single<TaskRepository> {
                TaskRespositoryImpl(get())
            }
        }
    }

    private fun databaseModule(): Module {
        return module {
            single { AppDatabase.getInstance(androidApplication()) }
        }
    }
}