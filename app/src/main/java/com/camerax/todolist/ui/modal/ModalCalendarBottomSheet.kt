package com.camerax.todolist.ui.modal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.camerax.todolist.databinding.ModalBottomSheetContentBinding
import com.camerax.todolist.ui.addtask.IModalCalendarBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*

class ModalCalendarBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: ModalBottomSheetContentBinding
    private lateinit var IModalCalendarBottomSheet: IModalCalendarBottomSheet

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ModalBottomSheetContentBinding.inflate(inflater, container, false)

        val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from<View>(binding.standardBottomSheet)
        behavior.skipCollapsed = false
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        bindCalendarView()
        bindListeners()


        return binding.root
    }

    private fun bindListeners() {
        binding.btnConfirm.setOnClickListener {
            IModalCalendarBottomSheet.onClick(timestampDate)
        }

        binding.btnToday.setOnClickListener {
            timestampDate = currentDate
            binding.calendarView.date = timestampDate
            binding.btnToday.visibility = View.GONE
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun bindCalendarView() {
        binding.calendarView.minDate = currentDate
        if (timestampDate.toString() == ("0")) {
            timestampDate = currentDate
            binding.btnToday.visibility = View.GONE
        }
        binding.calendarView.date = timestampDate
        binding.calendarView.setOnDateChangeListener { _, year, month, day ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, day)

            val time = SimpleDateFormat("dd/M/yyyy").format(Date(calendar.timeInMillis))
            val time2 = SimpleDateFormat("dd/M/yyyy").format(Date(currentDate))
            binding.btnToday.visibility = if (time == time2) {
                View.GONE
            } else {
                View.VISIBLE
            }
        timestampDate = calendar.timeInMillis
        }
    }

    fun listener(IModalCalendarBottomSheet: IModalCalendarBottomSheet) {
        this.IModalCalendarBottomSheet = IModalCalendarBottomSheet
    }

    companion object {
        const val TAG = "ModalBottomSheet"
        var timestampDate: Long = 0
        val currentDate = Calendar.getInstance().timeInMillis
    }
}