package com.camerax.todolist.ui.main

import com.camerax.todolist.data.model.TaskResponseValue

interface IModalTaskDetailsBottomSheet {
    fun onClickDeleteTask(task: TaskResponseValue)
    fun onClickUpdateTask(task: TaskResponseValue)
    fun onClickEditTask(task: TaskResponseValue)

}