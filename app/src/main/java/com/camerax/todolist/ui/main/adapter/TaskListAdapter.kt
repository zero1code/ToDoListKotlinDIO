package com.camerax.todolist.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.camerax.todolist.R
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.databinding.ItemTaskBinding
import java.util.*
import java.util.concurrent.TimeUnit

class TaskListAdapter : ListAdapter<TaskResponseValue, TaskListAdapter.TaskViewHolder>(DiffCallback()) {

    var listenerTaskDetails: (TaskResponseValue) -> Unit = {}
    var listenerDelete: (TaskResponseValue) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val context: Context = holder.itemView.context
        holder.bind(getItem(position), context)
    }

    inner class TaskViewHolder(
        private val binding: ItemTaskBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceAsColor")
        fun bind(item: TaskResponseValue, context: Context) {
            val c = Calendar.getInstance()
            c.timeInMillis = item.timestamp_date * 1000
            val date = c.get(Calendar.DAY_OF_MONTH)
            val weekdate =
                c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
            binding.tvTitle.text = item.title
            binding.tvDate.text = date.toString()
            binding.tvWeekDate.text = weekdate?.substring(0, weekdate.length - 1)
            if (item.description.isEmpty()) {
                binding.tvDescription.visibility = View.INVISIBLE
            } else {
                binding.tvDescription.visibility = View.VISIBLE
                binding.tvDescription.text = item.description
            }
            binding.container.setOnClickListener {
                listenerTaskDetails(item)
            }

            val currentDate = Date().time
            val taskDate = item.timestamp_date * 1000
            val diferenceDates = TimeUnit.MILLISECONDS.toMillis(taskDate - currentDate)
            if (item.task_completed == 1L) {
                binding.cvWeekDate.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.ripple_green
                    )
                )
            } else if (item.task_completed == 1L && diferenceDates < 0) {
                binding.cvWeekDate.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.ripple_green
                    )
                )
            } else if (item.task_completed == 0L && diferenceDates < 0) {
                binding.cvWeekDate.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.ripple_red
                    )
                )
            } else {
                binding.cvWeekDate.background.setTint(
                    ContextCompat.getColor(
                        context,
                        R.color.ripple_orange
                    )
                )
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<TaskResponseValue>() {
        override fun areItemsTheSame(oldItem: TaskResponseValue, newItem: TaskResponseValue) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: TaskResponseValue, newItem: TaskResponseValue) =
            oldItem.id == newItem.id

    }
}

