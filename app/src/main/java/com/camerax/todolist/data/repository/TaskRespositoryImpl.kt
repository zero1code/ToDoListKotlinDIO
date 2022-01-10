package com.camerax.todolist.data.repository

import com.camerax.todolist.data.database.AppDatabase
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TaskRespositoryImpl(
    appDatabase: AppDatabase,
) : TaskRepository {

    private val dao = appDatabase.taskDao()

    override suspend fun save(task: TaskResponseValue) {
        dao.save(task)
    }

    override fun list(): Flow<List<TaskResponseValue>> {
        return dao.findAll()
    }

    override suspend fun delete(id: Long) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.deleteTask(id)
        }
    }

    override suspend fun update(task: TaskResponseValue) {
        GlobalScope.launch(Dispatchers.IO) {
            dao.updateTask(task)
        }
    }
}