package com.camerax.todolist.domain

import com.camerax.todolist.core.UseCase
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class ListTaskByDateUseCase(
    private val repository: TaskRepository
) : UseCase.WithParam<String>() {
    override suspend fun execute(param: String): Flow<List<TaskResponseValue>> {
        return repository.findByDate(param)
    }
}