package com.camerax.todolist.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.camerax.todolist.core.extensions.createDialog
import com.camerax.todolist.core.extensions.createProgressDialog
import com.camerax.todolist.core.extensions.format
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.databinding.ActivityMainBinding
import com.camerax.todolist.presentation.MainViewModel
import com.camerax.todolist.ui.addtask.AddTaskActivity
import com.camerax.todolist.ui.addtask.AddTaskActivity.Companion.TASK_ID
import com.camerax.todolist.ui.addtask.IModalCalendarBottomSheet
import com.camerax.todolist.ui.addtask.ModalCalendarBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dialog by lazy { createProgressDialog() }
    private val viewModel by viewModel<MainViewModel>()
    private val adapter by lazy { TaskListAdapter() }
    private val modalBottomSheet by lazy { ModalTaskDetailsBottomSheet() }
    private lateinit var IModalTaskDetailsBottomSheet: IModalTaskDetailsBottomSheet

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
                    binding.includeEmpty.emptyState.visibility =
                        if (it.list.isEmpty()) View.VISIBLE else View.GONE
                    adapter.submitList(it.list)
                }
            }
        }
    }

    private fun insertListeners() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddTaskActivity::class.java))
        }

        adapter.listenerTaskDetails = {
            val args = Bundle()
            args.putSerializable("task", it)
            modalBottomSheet.arguments = args
            modalBottomSheet.show(supportFragmentManager, ModalTaskDetailsBottomSheet.TAG)
            IModalTaskDetailsBottomSheet = object : IModalTaskDetailsBottomSheet {
                override fun onClickUpdateTask(task: TaskResponseValue) {
                    viewModel.updateTask(task)
                    modalBottomSheet.dismiss()
                }

                override fun onClickEditTask(task: TaskResponseValue) {
                    startActivity(Intent(this@MainActivity, AddTaskActivity::class.java).putExtra("task", task))
                    modalBottomSheet.dismiss()
                }

                override fun onClickDeleteTask(task: TaskResponseValue) {
                    viewModel.deleteTask(task.id)
                    modalBottomSheet.dismiss()
                }
            }
            modalBottomSheet.listener(IModalTaskDetailsBottomSheet)
        }

        adapter.listenerDelete = {
            viewModel.deleteTask(it.id)
        }
    }
}