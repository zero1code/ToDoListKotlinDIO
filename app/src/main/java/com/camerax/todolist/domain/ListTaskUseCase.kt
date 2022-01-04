package com.camerax.todolist.domain

import com.camerax.todolist.core.UseCase
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class ListTaskUseCase(
    private val repository: TaskRepository
) : UseCase.NoParam<List<TaskResponseValue>>() {
    override suspend fun execute(): Flow<List<TaskResponseValue>> {
        return repository.list()
    }
}