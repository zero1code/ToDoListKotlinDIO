package com.camerax.todolist.data.repository

import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun save(task: TaskResponseValue)
    fun list():Flow<List<TaskResponseValue>>
    suspend fun delete(id: Long)
}