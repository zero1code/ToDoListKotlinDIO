package com.camerax.todolist.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.camerax.todolist.data.model.TaskResponseValue
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query(value = "SELECT * FROM tb_tasks")
    fun findAll(): Flow<List<TaskResponseValue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(entity: TaskResponseValue)
}