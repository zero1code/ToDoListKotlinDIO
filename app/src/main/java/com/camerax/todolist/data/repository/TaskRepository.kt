package com.camerax.todolist.data.repository

import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.model.CalendarModel
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun save(task: TaskResponseValue)
    fun list():Flow<List<TaskResponseValue>>
    fun findByDate(date: String):Flow<List<TaskResponseValue>>
    suspend fun delete(id: Long)
    suspend fun update(task: TaskResponseValue)
}