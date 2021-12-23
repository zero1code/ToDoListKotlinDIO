package com.camerax.todolist.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.camerax.todolist.R
import com.camerax.todolist.databinding.ActivityAddTaskBinding
import com.camerax.todolist.datasource.TaskDataSource
import com.camerax.todolist.extensions.format
import com.camerax.todolist.extensions.text
import com.camerax.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {
            binding.btnNewTask.text = getString(R.string.label_edit_task)
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.tilTitle.text = it.title
                binding.tilDate.text = it.date
                binding.tilHour.text = it.hour
            }
        }

        insertListeners()
    }

    private fun insertListeners() {
        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.tilHour.editText?.setOnClickListener {
            val timerPicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timerPicker.addOnPositiveButtonClickListener {
                val minute = if (timerPicker.minute in 0..9) "0${timerPicker.minute}" else timerPicker.minute

                val hour = if (timerPicker.hour in 0..9) "0${timerPicker.hour}" else timerPicker.hour

                binding.tilHour.text = "${hour}:${minute}"
            }
            timerPicker.show(supportFragmentManager, null)
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnNewTask.setOnClickListener {
            val task = Task(
                title = binding.tilTitle.text,
                date = binding.tilDate.text,
                hour = binding.tilHour.text,
                id = intent.getIntExtra(TASK_ID, 0)
            )
            TaskDataSource.insertTask(task)

            setResult(RESULT_OK)
            finish()
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }
}