package com.camerax.todolist.presentation.di

import com.camerax.todolist.presentation.AddTaskViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object PresentationModule {
    fun load() {
        loadKoinModules(viewModelModules())
    }

    private fun viewModelModules(): Module {
        return module {
            viewModel { AddTaskViewModel(get()) }
        }
    }
}