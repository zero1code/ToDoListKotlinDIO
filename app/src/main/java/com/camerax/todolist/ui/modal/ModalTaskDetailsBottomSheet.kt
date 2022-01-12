package com.camerax.todolist.ui.modal

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.camerax.todolist.R
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.databinding.ModalBottomSheetTaskDetailsBinding
import com.camerax.todolist.ui.main.IModalTaskDetailsBottomSheet
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ModalTaskDetailsBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: ModalBottomSheetTaskDetailsBinding
    private lateinit var IModalTaskDetailsBottomSheet: IModalTaskDetailsBottomSheet
    private lateinit var task: TaskResponseValue
    lateinit var countDownTimer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ModalBottomSheetTaskDetailsBinding.inflate(inflater, container, false)

        val behavior: BottomSheetBehavior<*> =
            BottomSheetBehavior.from<View>(binding.standardBottomSheet)
        behavior.skipCollapsed = false
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        val mArgs = arguments
        task = mArgs?.get("task") as TaskResponseValue

        bindDetails(task)

        bindListeners()

        return binding.root
    }

    private fun bindDetails(task: TaskResponseValue) {

        binding.btnConfirm.visibility = if (task.task_completed == 1L) View.GONE else View.VISIBLE

        binding.tvTitle.text = task.title
        binding.tvDescription.text =
            if (task.description.isNotEmpty()) task.description else getString(R.string.label_no_description)

        val sdf = SimpleDateFormat("E, dd 'de' MMM 'de' yyyy")
        val currentDate = Date().time
        val taskDate = task.timestamp_date * 1000
        val diferenceDates = taskDate.minus(currentDate)

        binding.tvDate.text = "${sdf.format(Date(taskDate))} às ${task.hour}"


        val reminderDate = task.timestamp_alarm * 1000
        if (reminderDate != 0L) {
            val diferenceAlarm = taskDate.minus(reminderDate) / 1000
            val hoursAgo = diferenceAlarm / 3600
            val minAgo = (diferenceAlarm % 3600) / 60

            if (hoursAgo > 0 && minAgo > 0) {
                binding.tvDateReminder.text =
                    "$hoursAgo hora${if (hoursAgo > 1) "s" else ""} e $minAgo minuto${if (minAgo > 1) "s" else ""} antes"
            } else if (hoursAgo > 0 && minAgo <= 0) {
                binding.tvDateReminder.text = "$hoursAgo hora${if (hoursAgo > 1) "s" else ""} antes"
            } else if (hoursAgo <= 0 && minAgo > 0) {
                binding.tvDateReminder.text = "$minAgo minuto${if (minAgo > 1) "s" else ""} antes"
            } else {
                binding.tvDateReminder.text = "O lembrete não foi definido."
            }
        } else {
            binding.tvDateReminder.text = "O lembrete não foi definido."
        }

        val daysAgo = TimeUnit.MILLISECONDS.toDays(diferenceDates) + 1
        if (task.task_completed == 1L) {
            binding.tvDateTimer.text = getString(R.string.label_task_finished)
        } else {
            if (daysAgo > 1 && task.task_completed == 0L) {
                binding.tvDateTimer.text =
                    "Restam $daysAgo dia${if (daysAgo > 1) "s" else ""} para a data da tarefa."
            } else {
                countDownTimer(binding.tvDateTimer, diferenceDates)
            }
        }

        val animationblink = AnimationUtils.loadAnimation(this.requireContext(), R.anim.blink2)
        binding.cvIcon.startAnimation(animationblink)
        if (task.task_completed == 1L) {
            binding.cvIcon.background.setTint(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.green
                )
            )
        } else if (task.task_completed == 1L && diferenceDates < 0) {
            binding.cvIcon.background.setTint(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.green
                )
            )
        } else if (task.task_completed == 0L && diferenceDates < 0) {
            binding.cvIcon.background.setTint(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.red
                )
            )
        } else {
            binding.cvIcon.background.setTint(
                ContextCompat.getColor(
                    this.requireContext(),
                    R.color.orange
                )
            )
        }
    }

    private fun countDownTimer(tvDateTimer: TextView, toMillis: Long) {
        countDownTimer = object : CountDownTimer(toMillis, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                var diff = millisUntilFinished
                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                val elapsedDays = diff / daysInMilli
                diff %= daysInMilli

                val elapsedHours = diff / hoursInMilli
                diff %= hoursInMilli

                val elapsedMinutes = diff / minutesInMilli
                diff %= minutesInMilli

                val elapsedSeconds = diff / secondsInMilli

                var format =
                    String.format("%02d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds)

                tvDateTimer.text = "Faltam ${if (task.task_completed == 1L) getString(R.string.label_task_finished) else format} para a data da tarefa"
            }

            override fun onFinish() {
                tvDateTimer.text = if (task.task_completed == 1L) getString(R.string.label_task_finished) else getString(R.string.label_task_not_finished)
            }
        }.start()
    }


    private fun bindListeners() {
        binding.btnConfirm.setOnClickListener {
            IModalTaskDetailsBottomSheet.onClickUpdateTask(task.copy(task_completed = 1))
        }

        binding.btnEditTask.setOnClickListener {
            IModalTaskDetailsBottomSheet.onClickEditTask(task)
        }

        binding.btnDeleteTask.setOnClickListener {
            IModalTaskDetailsBottomSheet.onClickDeleteTask(task)
        }
    }

    fun listener(IModalTaskDetailsBottomSheet: IModalTaskDetailsBottomSheet) {
        this.IModalTaskDetailsBottomSheet = IModalTaskDetailsBottomSheet
    }

    companion object {
        const val TAG = "ModalBottomSheet"
        var timestampDate: Long = 0
        val currentDate = Calendar.getInstance().timeInMillis
    }
}