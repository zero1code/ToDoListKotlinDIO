package com.camerax.todolist.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.camerax.todolist.core.extensions.createDialog
import com.camerax.todolist.core.extensions.createProgressDialog
import com.camerax.todolist.databinding.ActivityMainBinding
import com.camerax.todolist.presentation.MainViewModel
import com.camerax.todolist.ui.addtask.AddTaskActivity
import com.camerax.todolist.ui.addtask.AddTaskActivity.Companion.TASK_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dialog by lazy { createProgressDialog() }
    private val viewModel by viewModel<MainViewModel>()
    private val adapter by lazy { TaskListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTasks.adapter = adapter

        bindObserve()
        insertListeners()

        lifecycle.addObserver(viewModel)
    }

    private fun bindObserve() {
        viewModel.state.observe(this) {
            when (it) {
                MainViewModel.State.Loading -> {
                    dialog.show()
                }
                is MainViewModel.State.Error -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage(it.error.message)
                    }.show()
                }
                is MainViewModel.State.Success -> {
                    dialog.dismiss()
                    binding.includeEmpty.emptyState.visibility =
                        if (it.list.isEmpty()) View.VISIBLE else View.GONE
                    adapter.submitList(it.list)
                }
                is MainViewModel.State.Deleted -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage("Tarefa excluída com sucesso!")
                    }.show()
//                    binding.includeEmpty.emptyState.visibility =
//                        if (it.list.isEmpty()) View.VISIBLE else View.GONE
//                    adapter.submitList(it.list)
                }
            }
        }
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
            viewModel.deleteTask(it.id)
        }
    }
}