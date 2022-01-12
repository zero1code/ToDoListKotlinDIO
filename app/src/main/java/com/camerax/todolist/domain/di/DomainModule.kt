package com.camerax.todolist.domain.di

import com.camerax.todolist.domain.*
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

object DomainModule {
    fun load() {
        loadKoinModules(useCaseModules())
    }

    private fun useCaseModules(): Module {
        return module {
            factory { SaveTaskUseCase(get()) }
            factory { DeleteTaskUseCase(get()) }
            factory { ListTaskUseCase(get()) }
            factory { ListTaskByDateUseCase(get()) }
            factory { UpdateTaskUseCase(get()) }
        }
    }

}