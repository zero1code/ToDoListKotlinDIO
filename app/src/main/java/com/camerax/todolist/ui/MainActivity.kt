package com.camerax.todolist.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.camerax.todolist.databinding.ActivityMainBinding
import com.camerax.todolist.datasource.TaskDataSource
import com.camerax.todolist.ui.AddTaskActivity.Companion.TASK_ID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { TaskListAdapter() }

    private val register =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) updateList()
        }

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
            register.launch(Intent(this, AddTaskActivity::class.java))
        }

        adapter.listenerEdit = {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(TASK_ID, it.id)
            register.launch(intent)
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