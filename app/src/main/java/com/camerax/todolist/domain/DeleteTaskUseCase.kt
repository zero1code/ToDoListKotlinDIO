package com.camerax.todolist.domain

import com.camerax.todolist.core.UseCase
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteTaskUseCase(
    private val repository: TaskRepository
): UseCase.NoSource<Long>() {
    override suspend fun execute(param: Long): Flow<Unit> {
        return flow {
            repository.delete(param)
            emit(Unit)
        }
    }
}