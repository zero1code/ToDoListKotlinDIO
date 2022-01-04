package com.camerax.todolist.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.camerax.todolist.R
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.databinding.ActivityConfirmTaskBinding

class ConfirmTaskActivity : AppCompatActivity() {
    private val binding by lazy { ActivityConfirmTaskBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        val task = intent.getSerializableExtra("task_id") as TaskResponseValue

        binding.btnBack.setOnClickListener {
            finish()
        }


    }
}