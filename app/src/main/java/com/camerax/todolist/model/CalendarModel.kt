package com.camerax.todolist.model

import java.text.SimpleDateFormat
import java.util.*

data class CalendarModel(
    var data: Date,
    var isSelected: Boolean = false
) {

    val calendarDay: String
        get() = SimpleDateFormat("E", Locale.getDefault()).format(data).substring(0, 3)

    val calendarDate: String
        get() {
            val cal = Calendar.getInstance()
            cal.time = data
            return cal[Calendar.DAY_OF_MONTH].toString()
        }

    val calendarFullDate: String
        get() = SimpleDateFormat("E, dd 'de' MMM", Locale.getDefault()).format(data)

}
