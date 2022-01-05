package com.camerax.todolist.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.camerax.todolist.databinding.ActivityMainBinding
import com.camerax.todolist.data.TaskDataSource
import com.camerax.todolist.ui.addtask.AddTaskActivity
import com.camerax.todolist.ui.addtask.AddTaskActivity.Companion.TASK_ID
import com.camerax.todolist.ui.TaskListAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { TaskListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTasks.adapter = adapter
        updateList()

        insertListeners()
    }

    private fun insertListeners() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        adapter.listenerEdit = {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(TASK_ID, it.id)
            startActivity(intent)
        }

        adapter.listenerDelete = {
           TaskDataSource.deleteTask(it)
            updateList()
        }
    }

    private fun updateList() {

        val list = TaskDataSource.getList()

        binding.includeEmpty.emptyState.visibility = if (list.isEmpty())  View.VISIBLE else View.GONE

        adapter.submitList(null)
        adapter.submitList(list)
    }
}