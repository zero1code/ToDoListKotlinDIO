package com.camerax.todolist.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

typealias TaskResponse = HashMap<String, TaskResponseValue>

@Entity(tableName = "tb_tasks")
data class TaskResponseValue(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String,
    val date: String,
    val hour: String,
    val description: String,
    val remember_task: String,
    val remember_time: String,
    val timestamp_date: Long,
    val timestamp_alarm: Long

) : Serializable