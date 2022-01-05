package com.camerax.todolist.ui.confirmtask

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import com.camerax.todolist.R
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.databinding.ActivityConfirmTaskBinding

class ConfirmTaskActivity : AppCompatActivity() {
    private val binding by lazy { ActivityConfirmTaskBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val task = intent.getSerializableExtra("task_id") as TaskResponseValue

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.tvTitle.text = Html.fromHtml("<b>${task.title}</b>", 0)
            binding.tvDate.text = Html.fromHtml("<b>Data: </b>${task.date} às ${task.hour}.", 0)
            binding.tvDescription.text = Html.fromHtml("<b>Descrição: </b>${task.description}", 0)

        } else {
            binding.tvTitle.text = Html.fromHtml("<b>${task.title}</b>")
            binding.tvDate.text = Html.fromHtml("<b>Data: </b>${task.date} às ${task.hour}.")
            binding.tvDescription.text = Html.fromHtml("<b>Descrição: </b>${task.description}")

        }

        if (task.id.equals(0)) {
            binding.tvConfDetails.text = getString(R.string.label_no_remember_task_description)
        } else {
            binding.tvConfDetails.text = getString(R.string.label_remember_task_description)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}