package com.camerax.todolist.domain

import com.camerax.todolist.core.UseCase
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SaveTaskUseCase(
    private val repository: TaskRepository
): UseCase.NoSource<TaskResponseValue>() {
    override suspend fun execute(param: TaskResponseValue): Flow<Unit> {
        return flow {
            repository.save(param)
            emit(Unit)
        }
    }
}