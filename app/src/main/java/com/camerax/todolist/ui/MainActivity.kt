package com.camerax.todolist.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.camerax.todolist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adpter by lazy { TaskListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTasks.adapter = adpter

        insertListeners()
    }

    private fun insertListeners() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }
    }
}