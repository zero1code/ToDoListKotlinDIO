package com.camerax.todolist.data.database.dao

import androidx.room.*
import com.camerax.todolist.data.model.TaskResponseValue
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query(value = "SELECT * FROM tb_tasks")
    fun findAll(): Flow<List<TaskResponseValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: TaskResponseValue): Long

    @Query("DELETE from tb_tasks where id = :id")
    fun deleteTask(id: Long)

    @Update
    fun updateTask(task: TaskResponseValue)
}