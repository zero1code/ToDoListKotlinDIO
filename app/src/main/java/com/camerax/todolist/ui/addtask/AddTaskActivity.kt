package com.camerax.todolist.ui.addtask

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.system.Os.accept
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.sqlite.db.SupportSQLiteCompat.Api16Impl.cancel
import com.camerax.todolist.R
import com.camerax.todolist.core.extensions.createDialog
import com.camerax.todolist.core.extensions.createProgressDialog
import com.camerax.todolist.databinding.ActivityAddTaskBinding
import com.camerax.todolist.data.TaskDataSource
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.core.extensions.format
import com.camerax.todolist.core.extensions.text
import com.camerax.todolist.presentation.AddTaskViewModel
import com.camerax.todolist.ui.confirmtask.ConfirmTaskActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Result.Companion.success


class AddTaskActivity : AppCompatActivity() {

    private val viewModel by viewModel<AddTaskViewModel>()
    private val dialog by lazy { createProgressDialog() }
    private val binding by lazy { ActivityAddTaskBinding.inflate(layoutInflater) }
    private val modalBottomSheet by lazy { ModalBottomSheet() }
    private lateinit var task: TaskResponseValue
    private var selectedData: Long = 0
    private var alarmMinute = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        if (intent.hasExtra(TASK_ID)) {
//            binding.btnNewTask.text = getString(R.string.label_edit_task)
//            val taskId = intent.getIntExtra(TASK_ID, 0)
//            TaskDataSource.findById(taskId)?.let {
//                binding.tilTitle.text = it.title
//                binding.tilDate.text = it.date
//                binding.tilHour.text = it.hour
//            }
//        }

        bindObserve()

        insertListeners()
        tieListeners()
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

    private fun addTaskType() {


        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_add_task_type, null)

        val editText = dialogView.findViewById<EditText>(R.id.tiet_new_task_type)


        MaterialAlertDialogBuilder(this)
            .setTitle("Adicionar tipo")
            .setView(dialogView)
            .setNegativeButton(resources.getString(R.string.label_cancel)) { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton(resources.getString(R.string.label_save)) { dialog, which ->
                Toast.makeText(this, editText.text, Toast.LENGTH_SHORT).show()
            }
            .show()
    }

    private fun insertListeners() {
        binding.chDate.setOnClickListener {
            modalBottomSheet.show(supportFragmentManager, ModalBottomSheet.TAG)


//            val datePicker = MaterialDatePicker.Builder.datePicker().build()
//            datePicker.addOnPositiveButtonClickListener {
//                val timeZone = TimeZone.getDefault()
//                val offset = timeZone.getOffset(Date().time) * -1
//                selectedData = it + offset
//                binding.chDate.text = Date(it + offset).format()
//                binding.btnClearChips.visibility = View.VISIBLE
//            }
//            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.chTime.setOnClickListener {
            val timerPicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
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
            binding.chipReminderGroup.visibility = View.VISIBLE
            binding.btnAddReminder.visibility = View.INVISIBLE
        }

        binding.chReminderDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
                 datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.chReminderDate.text = Date(it + offset).format()
                binding.btnClearChipsReminder.visibility = View.VISIBLE
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.chReminderTime.setOnClickListener {
            val timerPicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timerPicker.addOnPositiveButtonClickListener {
                val minute =
                    if (timerPicker.minute in 0..9) "0${timerPicker.minute}" else timerPicker.minute

                val hour =
                    if (timerPicker.hour in 0..9) "0${timerPicker.hour}" else timerPicker.hour

                binding.chReminderTime.text = "${hour}:${minute}"
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

                val sdfd = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

                val date = sdfd.parse("${binding.chDate.text} ${binding.chTime.text}")
                val timestampDate = (date.time.div(1000))

                val reminder = sdfd.parse("${binding.chReminderDate.text} ${binding.chReminderTime.text}")
                val timestampAlarm = (reminder.time.div(1000))


                task = TaskResponseValue(
                    id = 0,
                    title = binding.tilTitle.text,
                    description = binding.tilDescription.text,
                    date = binding.chDate.text.toString(),
                    hour = binding.chTime.text.toString(),
                    reminder_date = binding.chReminderDate.text.toString(),
                    reminder_time = binding.chReminderTime.text.toString(),
                    timestamp_date = timestampDate,
                    timestamp_alarm = timestampAlarm
                )
                viewModel.saveTask(task)

                setResult(RESULT_OK)
            }
        }
    }

    private fun checkFormulario(): Boolean {
        if (binding.tilTitle.text.isEmpty()) {
            binding.tilTitle.error = "Defina um título."
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
                AddTaskViewModel.State.Saved -> {
                    dialog.dismiss()
                    binding.appBar.visibility = View.GONE
                    binding.groupTask.visibility = View.GONE
                    binding.confirmAnimation.visibility = View.VISIBLE
                    binding.confirmAnimation.playAnimation()
                    binding.confirmAnimation.addAnimatorListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(animation: Animator?) {}
                        override fun onAnimationCancel(animation: Animator?) {}
                        override fun onAnimationStart(animation: Animator?) {}


                        override fun onAnimationEnd(animation: Animator?) {
                            startActivity(Intent(this@AddTaskActivity, ConfirmTaskActivity::class.java).putExtra(
                                TASK_ID, task))
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