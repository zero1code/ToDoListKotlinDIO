package com.camerax.todolist.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.camerax.todolist.R
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.databinding.ItemTaskBinding
import com.camerax.todolist.model.Task

class TaskListAdapter : ListAdapter<TaskResponseValue, TaskListAdapter.TaskViewHolder>(DiffCallback()) {

    var listenerEdit : (TaskResponseValue) -> Unit = {}
    var listenerDelete : (TaskResponseValue) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTaskBinding.inflate(inflater, parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(
            private val binding: ItemTaskBinding
        ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TaskResponseValue) {
            binding.tvTitle.text = item.title
            binding.tvDate.text = "${item.date} às ${item.hour}"
            binding.ivMore.setOnClickListener {
                showPopUp(item)
            }
        }

        private fun showPopUp(item: TaskResponseValue) {
            val ivMore = binding.ivMore
            val popupMenu = PopupMenu(ivMore.context, ivMore)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_edit -> listenerEdit(item)
                    R.id.action_delete -> listenerDelete(item)
                }
                return@setOnMenuItemClickListener true
            }
            popupMenu.show()
        }

    }
}

class DiffCallback : DiffUtil.ItemCallback<TaskResponseValue>() {
    override fun areItemsTheSame(oldItem: TaskResponseValue, newItem: TaskResponseValue) = oldItem == newItem

    override fun areContentsTheSame(oldItem: TaskResponseValue, newItem: TaskResponseValue) = oldItem.id == newItem.id

}