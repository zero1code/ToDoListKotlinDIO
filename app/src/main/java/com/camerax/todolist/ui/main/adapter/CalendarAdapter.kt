package com.camerax.todolist.ui.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.camerax.todolist.R
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.databinding.AdapterDaysOfYearBinding
import com.camerax.todolist.model.CalendarModel
import com.camerax.todolist.ui.main.MainActivity.Companion.firstOpenActivity
import java.util.*

class CalendarAdapter(
    private val listener: (calendarDateModel: CalendarModel, position: Int) -> Unit,
    private var taskList: List<TaskResponseValue>
) : RecyclerView.Adapter<CalendarAdapter.CalViewHolder>() {

    private val list = ArrayList<CalendarModel>()
    private val currentDate = Date().time

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = AdapterDaysOfYearBinding.inflate(inflater, parent, false)
        return CalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class CalViewHolder(
        private val binding: AdapterDaysOfYearBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CalendarModel) {
            val calendarDay = binding.tvCalendarDay
            val calendarDate = binding.tvCalendarDate
            val cardView = binding.cardCalendar
            val taskCompleted = binding.icTaskCompleted
            val taskNotCompleted = binding.icTaskNotComleted
            val taskWait = binding.icTaskWait
            val cal = Calendar.getInstance()
            val monthCalendar = cal.clone() as Calendar
            cal.time = monthCalendar.time

            taskCompleted.visibility = View.GONE
            taskNotCompleted.visibility = View.GONE
            taskWait.visibility = View.GONE

            for (task in taskList) {
                if (task.date == item.calendarFullDate) {

                    val taskDate = task.timestamp_date * 1000
                    val diferenceDates = taskDate.minus(currentDate)

                    if (task.task_completed == 1L) {
                        taskCompleted.visibility = View.VISIBLE
                    } else if (task.task_completed == 1L && diferenceDates < 0) {
                        taskCompleted.visibility = View.VISIBLE
                    } else if (task.task_completed == 0L && diferenceDates < 0) {
                        taskNotCompleted.visibility = View.VISIBLE
                    } else if (task.task_completed == 0L && diferenceDates > 0) {
                        taskWait.visibility = View.VISIBLE
                    } else {
                        taskCompleted.visibility = View.GONE
                        taskNotCompleted.visibility = View.GONE
                        taskWait.visibility = View.GONE
                    }
                }
            }


            if (item.calendarDate == cal[Calendar.DAY_OF_MONTH].toString() && firstOpenActivity) {
                firstOpenActivity = false
                item.isSelected = true
            }

            if (item.isSelected) {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.card_selected
                    )
                )
            } else {
                calendarDay.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                calendarDate.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.black
                    )
                )
                cardView.setCardBackgroundColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.white
                    )
                )
            }

            calendarDay.text = item.calendarDay
            calendarDate.text = item.calendarDate
            cardView.setOnClickListener {
                listener.invoke(item, adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(calendarList: ArrayList<CalendarModel>) {
        list.clear()
        list.addAll(calendarList)
        notifyDataSetChanged()
    }
}