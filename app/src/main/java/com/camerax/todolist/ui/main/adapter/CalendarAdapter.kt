package com.camerax.todolist.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.camerax.todolist.R
import com.camerax.todolist.databinding.AdapterDaysOfYearBinding
import com.camerax.todolist.model.CalendarModel
import com.camerax.todolist.ui.main.MainActivity.Companion.firstOpenActivity
import java.util.*

class CalendarAdapter(private val listener: (calendarDateModel: CalendarModel, position: Int) -> Unit) : RecyclerView.Adapter<CalendarAdapter.CalViewHolder>() {

    private val list = ArrayList<CalendarModel>()

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
            val cal = Calendar.getInstance()
            val monthCalendar = cal.clone() as Calendar
            cal.time = monthCalendar.time

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