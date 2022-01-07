package com.camerax.todolist.data

import com.camerax.todolist.data.model.TaskResponseValue

object TaskDataSource {
    private val list = arrayListOf<TaskResponseValue>()

    fun getList() = list.toList()

    fun insertTask(task: TaskResponseValue) {
        if (task.id == 0L) {
            list.add(task.copy(id = list.size + 1L))
        } else {
            list.remove(task)
            list.add(task)
        }
    }

    fun findById(taskId: Long) = list.find { it.id == taskId }

    fun deleteTask(task: TaskResponseValue) {
        list.remove(task)
    }
}