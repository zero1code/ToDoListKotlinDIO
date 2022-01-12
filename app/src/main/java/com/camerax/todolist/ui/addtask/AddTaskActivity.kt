package com.camerax.todolist.ui.addtask

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.camerax.todolist.R
import com.camerax.todolist.core.extensions.createDialog
import com.camerax.todolist.core.extensions.createProgressDialog
import com.camerax.todolist.core.extensions.format
import com.camerax.todolist.core.extensions.text
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.databinding.ActivityAddTaskBinding
import com.camerax.todolist.presentation.AddTaskViewModel
import com.camerax.todolist.ui.confirmtask.ConfirmTaskActivity
import com.camerax.todolist.ui.modal.ModalCalendarBottomSheet
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Result.Companion.success


class AddTaskActivity : AppCompatActivity() {

    private val viewModel by viewModel<AddTaskViewModel>()
    private val dialog by lazy { createProgressDialog() }
    private val binding by lazy { ActivityAddTaskBinding.inflate(layoutInflater) }
    private val modalBottomSheet by lazy { ModalCalendarBottomSheet() }
    private lateinit var IModalCalendarBottomSheet: IModalCalendarBottomSheet
    private lateinit var task: TaskResponseValue
    private var reminderYear = 0L
    private var dateYear = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {finish()}

        bindObserve()

        bindEditTask()
        insertListeners()
        tieListeners()
    }

    private fun bindEditTask() {
        if (intent.hasExtra("task")) {
            task = intent.getSerializableExtra("task") as TaskResponseValue

            binding.tilTitle.text = task.title
            binding.tilDescription.text = task.description
        }
    }

    private fun tieListeners() {
        binding.tietTitle.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                binding.tilTitle.error = null
                setEndIconTintFromTheme(applicationContext, binding.tilTitle)
                binding.tilTitle.endIconDrawable =
                    applicationContext.getDrawable(R.drawable.ic_check)
            } else {
                binding.tilTitle.endIconDrawable = null
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun insertListeners() {
        binding.chDate.setOnClickListener {
            binding.chDate.clearAnimation()
            if (binding.chDate.text.contains("Data")) ModalCalendarBottomSheet.timestampDate =
                0 else ModalCalendarBottomSheet.timestampDate
            modalBottomSheet.show(supportFragmentManager, ModalCalendarBottomSheet.TAG)
            IModalCalendarBottomSheet = object : IModalCalendarBottomSheet {
                override fun onClick(selectedDate: Long) {
                    dateYear = SimpleDateFormat("yyyy").format(Date(selectedDate)).toLong()
                    binding.chDate.text = Date(selectedDate).format()
                    binding.btnClearChips.visibility = View.VISIBLE
                    ModalCalendarBottomSheet.timestampDate = selectedDate
                    modalBottomSheet.dismiss()
                }
            }
            modalBottomSheet.listener(IModalCalendarBottomSheet)
        }

        binding.chTime.setOnClickListener {
            binding.chTime.clearAnimation()
            val hourNow = SimpleDateFormat("HH").format(Date(Date().time)).toInt()
            val timerPicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hourNow)
                .setMinute(0)
                .build()
            timerPicker.addOnPositiveButtonClickListener {
                val minute =
                    if (timerPicker.minute in 0..9) "0${timerPicker.minute}" else timerPicker.minute

                val hour =
                    if (timerPicker.hour in 0..9) "0${timerPicker.hour}" else timerPicker.hour

                binding.chTime.text = "${hour}:${minute}"
            }
            timerPicker.show(supportFragmentManager, null)
        }

        binding.btnClearChips.setOnClickListener {
            binding.chDate.text = getString(R.string.label_date)
            binding.chTime.text = getString(R.string.label_hour)
            binding.btnClearChips.visibility = View.GONE
        }

        binding.btnAddReminder.setOnClickListener {

            if (binding.chReminderDate.text.contains("Data")) ModalCalendarBottomSheet.timestampDate =
                0 else ModalCalendarBottomSheet.timestampDate
            modalBottomSheet.show(supportFragmentManager, ModalCalendarBottomSheet.TAG)
            IModalCalendarBottomSheet = object : IModalCalendarBottomSheet {
                override fun onClick(selectedDate: Long) {
                    reminderYear = SimpleDateFormat("yyyy").format(Date(selectedDate)).toLong()
                    binding.chReminderDate.text = Date(selectedDate).format()
                    binding.btnClearChipsReminder.visibility = View.VISIBLE
                    binding.chipReminderGroup.visibility = View.VISIBLE
                    binding.btnAddReminder.visibility = View.INVISIBLE
                    ModalCalendarBottomSheet.timestampDate = selectedDate
                    modalBottomSheet.dismiss()
                }
            }
            modalBottomSheet.listener(IModalCalendarBottomSheet)
        }

        binding.chReminderDate.setOnClickListener {
            if (binding.chDate.text.contains("Data")) ModalCalendarBottomSheet.timestampDate =
                0 else ModalCalendarBottomSheet.timestampDate
            modalBottomSheet.show(supportFragmentManager, ModalCalendarBottomSheet.TAG)
            IModalCalendarBottomSheet = object : IModalCalendarBottomSheet {
                override fun onClick(selectedDate: Long) {
                    reminderYear = SimpleDateFormat("yyyy").format(Date(selectedDate)).toLong()
                    binding.chReminderDate.text = Date(selectedDate).format()
                    binding.btnClearChipsReminder.visibility = View.VISIBLE
                    ModalCalendarBottomSheet.timestampDate = selectedDate
                    modalBottomSheet.dismiss()
                }
            }
            modalBottomSheet.listener(IModalCalendarBottomSheet)
        }

        binding.chReminderTime.setOnClickListener {
            binding.chReminderTime.clearAnimation()
            val hourNow = SimpleDateFormat("HH").format(Date(Date().time)).toInt()
            val timerPicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hourNow)
                .setMinute(0)
                .build()
            timerPicker.addOnPositiveButtonClickListener {

                if (checkReminderTime(timerPicker.hour, timerPicker.minute)) {
                    val minute =
                        if (timerPicker.minute in 0..9) "0${timerPicker.minute}" else timerPicker.minute

                    val hour =
                        if (timerPicker.hour in 0..9) "0${timerPicker.hour}" else timerPicker.hour

                    binding.chReminderTime.text = "${hour}:${minute}"
                }
            }
            timerPicker.show(supportFragmentManager, null)
        }

        binding.btnClearChipsReminder.setOnClickListener {
            binding.chReminderDate.text = getString(R.string.label_date)
            binding.chReminderTime.text = getString(R.string.label_hour)
            binding.chipReminderGroup.visibility = View.INVISIBLE
            binding.btnClearChipsReminder.visibility = View.INVISIBLE
            binding.btnAddReminder.visibility = View.VISIBLE
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnNewTask.setOnClickListener {
            if (checkFormulario()) {

                val sdfd = SimpleDateFormat("E, dd 'de' MMM yyyy HH:mm", Locale.getDefault())

                val date = sdfd.parse("${binding.chDate.text} $dateYear ${binding.chTime.text}")
                val timestampDate = (date.time.div(1000))

                var timestampAlarm = 0L
                if (!binding.chReminderDate.text.contains("Data")) {
                    val reminder =
                        sdfd.parse("${binding.chReminderDate.text} $reminderYear ${binding.chReminderTime.text}")
                    timestampAlarm = (reminder.time.div(1000))
                }


                task = TaskResponseValue(
                    id = if (intent.hasExtra("task")) task.id else 0,
                    title = binding.tilTitle.text,
                    description = binding.tilDescription.text,
                    date = binding.chDate.text.toString(),
                    hour = binding.chTime.text.toString(),
                    reminder_date = binding.chReminderDate.text.toString(),
                    reminder_time = binding.chReminderTime.text.toString(),
                    timestamp_date = timestampDate,
                    timestamp_alarm = timestampAlarm,
                    task_completed = 0
                )
                if (task.id != 0L) {
                   viewModel.updateTask(task)
                } else {
                    viewModel.saveTask(task)
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun checkReminderTime(hourSelected: Int, minSelected: Int): Boolean {
        val hourNow = SimpleDateFormat("HH").format(Date(Date().time)).toInt()
        val minNow = SimpleDateFormat("mm").format(Date(Date().time)).toInt()
        val dateNow = SimpleDateFormat("dd").format(Date(Date().time)).toInt()
        val yearNow = SimpleDateFormat("yyyy").format(Date(Date().time)).toInt()
        val monthNow = SimpleDateFormat("M").format(Date(Date().time)).toInt()

        val dateSelected =
            SimpleDateFormat("dd").format(Date(ModalCalendarBottomSheet.timestampDate)).toInt()
        val yearSelected =
            SimpleDateFormat("yyyy").format(Date(ModalCalendarBottomSheet.timestampDate)).toInt()
        val monthSelected =
            SimpleDateFormat("M").format(Date(ModalCalendarBottomSheet.timestampDate)).toInt()

        if (hourNow == hourSelected) {
            if (minNow > minSelected && dateNow == dateSelected && yearNow == yearSelected) {
                createDialog {
                    setMessage("Não é possível criar um lembrete com a data menor que a atual: $dateNow/$monthNow/$yearNow $hourNow:$minNow.")
                }.show()
                return false
            }
        } else {
            if (hourNow > hourSelected && dateNow == dateSelected && yearNow == yearSelected) {
                createDialog {
                    setMessage("Não é possível criar um lembrete com a data menor que a atual: $dateNow/$monthNow/$yearNow $hourNow:$minNow.")
                }.show()
                return false
            }
        }

        return true
    }

    private fun checkFormulario(): Boolean {
        if (binding.tilTitle.text.isEmpty()) {
            binding.tilTitle.error = "Defina um título."
            return false
        }

        if (binding.chDate.text.contains("Data")) {
            val animationblink = AnimationUtils.loadAnimation(applicationContext, R.anim.blink)
            binding.chDate.startAnimation(animationblink)
            Snackbar.make(
                binding.root,
                getString(R.string.label_error_no_task_date),
                Snackbar.LENGTH_LONG
            )
                .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                .setActionTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .show()

            return false
        }

        if (binding.chTime.text.contains("Definir Horário")) {
            val animationblink = AnimationUtils.loadAnimation(applicationContext, R.anim.blink)
            binding.chTime.startAnimation(animationblink)
            Snackbar.make(
                binding.root,
                getString(R.string.label_error_no_task_time),
                Snackbar.LENGTH_LONG
            )
                .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                .setActionTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .show()

            return false
        }

        if (!binding.chReminderDate.text.contains("Data") && binding.chReminderTime.text.contains("Definir Horário")) {
            val animationblink = AnimationUtils.loadAnimation(applicationContext, R.anim.blink)
            binding.chReminderTime.startAnimation(animationblink)
            Snackbar.make(
                binding.root,
                getString(R.string.label_error_no_reminder_task_time),
                Snackbar.LENGTH_LONG
            )
                .setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.red))
                .setActionTextColor(ContextCompat.getColor(applicationContext, R.color.white))
                .show()

            return false
        }

        return true
    }

    private fun setEndIconTintFromTheme(context: Context, inputLayout: TextInputLayout) {
        // Force the tint to change. This, also, is probably a bug.
        inputLayout.setEndIconTintList(null)
        inputLayout.setEndIconTintList(getDefaultEndIconTint(context))
    }

    private fun getDefaultEndIconTint(context: Context): ColorStateList? {
        val typedValue = TypedValue()
        val theme: Resources.Theme = context.theme
        // textInputStyle controls styling for the TextInputLayout and is where endIconTint is
        // defined as @color/design_icon_tint
        theme.resolveAttribute(R.attr.textInputStyle, typedValue, true)

        // We have the styles attribute. Now get the tint value.
        val attrs = intArrayOf(R.color.green)
        val ta = obtainStyledAttributes(typedValue.data, attrs)
        val csl = ta.getColorStateList(0)
        ta.recycle()
        return csl
    }

    private fun bindObserve() {
        viewModel.state.observe(this) {
            when (it) {
                AddTaskViewModel.State.Loading -> dialog.show()
                is AddTaskViewModel.State.Error -> {
                    dialog.dismiss()
                    createDialog {
                        setMessage(it.error.message)
                    }.show()
                }
                is AddTaskViewModel.State.Success -> success(it)
                AddTaskViewModel.State.Updated -> {
                    dialog.dismiss()
                    binding.appBar.visibility = View.GONE
                    binding.groupTask.visibility = View.GONE
                    binding.confirmAnimation.visibility = View.VISIBLE
                    binding.confirmAnimation.playAnimation()
                    binding.confirmAnimation.addAnimatorListener(object :
                        Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}


                        override fun onAnimationEnd(animation: Animator?) {
                            startActivity(
                                Intent(
                                    this@AddTaskActivity,
                                    ConfirmTaskActivity::class.java
                                ).putExtra(
                                    TASK_ID, task
                                )
                            )
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            finish()
                        }
                    })
                }
                is AddTaskViewModel.State.Saved -> {
                    dialog.dismiss()
                    binding.appBar.visibility = View.GONE
                    binding.groupTask.visibility = View.GONE
                    binding.confirmAnimation.visibility = View.VISIBLE
                    binding.confirmAnimation.playAnimation()
                    binding.confirmAnimation.addAnimatorListener(object :
                        Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}


                        override fun onAnimationEnd(animation: Animator?) {
                            startActivity(
                                Intent(
                                    this@AddTaskActivity,
                                    ConfirmTaskActivity::class.java
                                ).putExtra(
                                    TASK_ID, task
                                )
                            )
                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                            finish()
                        }
                    })
                }
            }
        }
    }

    companion object {
        const val TASK_ID = "task_id"
    }
}