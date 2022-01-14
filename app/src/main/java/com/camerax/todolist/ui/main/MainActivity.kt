package com.camerax.todolist.ui.main

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.camerax.todolist.R
import com.camerax.todolist.core.extensions.createDialog
import com.camerax.todolist.core.extensions.createProgressDialog
import com.camerax.todolist.core.extensions.format
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.databinding.ActivityMainBinding
import com.camerax.todolist.model.CalendarModel
import com.camerax.todolist.presentation.MainViewModel
import com.camerax.todolist.ui.addtask.AddTaskActivity
import com.camerax.todolist.ui.main.adapter.CalendarAdapter
import com.camerax.todolist.ui.main.adapter.TaskListAdapter
import com.camerax.todolist.ui.modal.ModalTaskDetailsBottomSheet
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val dialog by lazy { createProgressDialog() }
    private val viewModel by viewModel<MainViewModel>()
    private val adapter by lazy { TaskListAdapter() }

    private lateinit var allTasks: List<TaskResponseValue>

    private val modalBottomSheet by lazy { ModalTaskDetailsBottomSheet() }
    private lateinit var IModalTaskDetailsBottomSheet: IModalTaskDetailsBottomSheet

    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val cal = Calendar.getInstance(Locale.getDefault())
    private val calendarList2 = ArrayList<CalendarModel>()
    private val dates = ArrayList<Date>()
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindObserve()
        insertListeners()

        lifecycle.addObserver(viewModel)

    }

    private fun bindObserve() {
        viewModel.state.observe(this) {
            when (it) {
                MainViewModel.State.Loading -> {
//                    dialog.show()
                }
                is MainViewModel.State.Error -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage(it.error.message)
                    }.show()
                }
                is MainViewModel.State.Success -> {
                    allTasks = it.list
                    setUpCalendarAdapter()
                    setUpCalendar()
                    calendarToCurrentDatePosition()
                }
                is MainViewModel.State.SuccessCalendar -> {
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
                    startActivity(
                        Intent(
                            this@MainActivity,
                            AddTaskActivity::class.java
                        ).putExtra("task", task)
                    )
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

        binding.ivCalendarNext.setOnClickListener {
            cal.add(Calendar.MONTH, 1)
            setUpCalendar()
        }
        binding.ivCalendarPrevious.setOnClickListener {
            cal.add(Calendar.MONTH, -1)
            setUpCalendar()
        }

        binding.btnToday.setOnClickListener {
            binding.btnToday.clearAnimation()
            val animationMove = AnimationUtils.loadAnimation(this, R.anim.move_from_right)
            binding.btnToday.startAnimation(animationMove)
            binding.btnToday.visibility = View.GONE
            firstOpenActivity = true
            changeDateOnce = 0
            cal.time = Date()
            setUpCalendar()
            calendarToCurrentDatePosition()
        }
    }

    private fun setUpCalendarAdapter() {
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.single_calendar_margin)
        binding.rvDaysOfYear.addItemDecoration(HorizontalItemDecoration(spacingInPixels))
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvDaysOfYear)

        calendarAdapter = CalendarAdapter({ calendarDateModel: CalendarModel, position: Int ->
            calendarList2.forEachIndexed { index, calendarModel ->
                calendarModel.isSelected = index == position
                daySelected = calendarDateModel.calendarFullDate
            }
            changeDateOnce++
            calendarAdapter.setData(calendarList2)
            viewModel.getTasksByDate(daySelected)
            if (daySelected != currentDate) {
                if (changeDateOnce == 1) {
                    val animationMove = AnimationUtils.loadAnimation(this, R.anim.move_from_left)
                    binding.btnToday.startAnimation(animationMove)
                    binding.btnToday.visibility = View.VISIBLE
                }
            } else {
                val animationMove = AnimationUtils.loadAnimation(this, R.anim.move_from_right)
                binding.btnToday.startAnimation(animationMove)
                binding.btnToday.visibility = View.GONE
                changeDateOnce = 0
            }
        }, allTasks)

        binding.rvDaysOfYear.adapter = calendarAdapter
    }

    private fun setUpCalendar() {
        val calendarList = ArrayList<CalendarModel>()
        binding.tvDateMonth.text = sdf.format(cal.time)
        val monthCalendar = cal.clone() as Calendar
        val maxDaysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        dates.clear()
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        while (dates.size < maxDaysInMonth) {
            dates.add(monthCalendar.time)
            calendarList.add(CalendarModel(monthCalendar.time))
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        calendarList2.clear()
        calendarList2.addAll(calendarList)
        calendarAdapter.setData(calendarList)
    }

    private fun calendarToCurrentDatePosition() {
        val cal = Calendar.getInstance()
        val monthCalendar = cal.clone() as Calendar
        val todayDate = cal.timeInMillis.div(1000) * 1000
        cal.time = monthCalendar.time
        val position = cal[Calendar.DAY_OF_MONTH]
        currentDate = Date(todayDate).format()
        viewModel.getTasksByDate(currentDate)
        binding.rvDaysOfYear.scrollToPosition(position - 3)
        binding.rvTasks.adapter = adapter
    }

    companion object {
        var firstOpenActivity = true
        var daySelected = ""
        var currentDate = ""
        var changeDateOnce = 0
    }

    inner class HorizontalItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position

            if (position != 0)
                outRect.left = space

            outRect.right = space
            outRect.bottom = space
            outRect.top = space
        }
    }
}