package com.camerax.todolist.ui

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.camerax.todolist.R
import com.camerax.todolist.core.extensions.createDialog
import com.camerax.todolist.core.extensions.createProgressDialog
import com.camerax.todolist.databinding.ActivityAddTaskBinding
import com.camerax.todolist.data.TaskDataSource
import com.camerax.todolist.data.model.TaskResponseValue
import com.camerax.todolist.core.extensions.format
import com.camerax.todolist.core.extensions.text
import com.camerax.todolist.presentation.AddTaskViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.Result.Companion.success


class AddTaskActivity : AppCompatActivity() {

    private val viewModel by viewModel<AddTaskViewModel>()
    private val dialog by lazy { createProgressDialog() }
    private val binding by lazy { ActivityAddTaskBinding.inflate(layoutInflater) }
    private lateinit var task: TaskResponseValue
    private var alarmHour = ""
    private var alarmMinute = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)) {
            binding.btnNewTask.text = getString(R.string.label_edit_task)
            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.tilTitle.text = it.title
                binding.tilDate.text = it.date
                binding.tilHour.text = it.hour
            }
        }

        bindObserve()

        val taskTypeItems =
            listOf("Material", "Design", "Components", "Android", "+ Adicionar Tipo")
        val taskTypeAdapter =
            ArrayAdapter(applicationContext, R.layout.list_item_task_type, taskTypeItems)
        (binding.actvTaskType as? AutoCompleteTextView)?.setAdapter(taskTypeAdapter)

        val warnedOptionsItems = listOf("Sim", "Não")
        val warnedAdapter =
            ArrayAdapter(applicationContext, R.layout.list_item_task_type, warnedOptionsItems)
        (binding.actvWarned as? AutoCompleteTextView)?.setAdapter(warnedAdapter)

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

        binding.tietDate.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                binding.tilDate.error = null
                setEndIconTintFromTheme(applicationContext, binding.tilDate)
                binding.tilDate.endIconDrawable =
                    applicationContext.getDrawable(R.drawable.ic_check)
            } else {
                binding.tilDate.endIconDrawable = null
            }
        }

        binding.tietHour.doOnTextChanged { text, start, before, count ->
            if (text!!.isNotEmpty()) {
                binding.tilHour.error = null
                setEndIconTintFromTheme(applicationContext, binding.tilHour)
                binding.tilHour.endIconDrawable =
                    applicationContext.getDrawable(R.drawable.ic_check)
            } else {
                binding.tilHour.endIconDrawable = null
            }
        }

        binding.actvTaskType.doOnTextChanged { text, start, before, count ->
            Log.d("TAG", "tieListeners: $text")
            if (text!!.contains("+ Adicionar Tipo")) {
                Toast.makeText(this, "Mostrar dialog", Toast.LENGTH_LONG).show()
            }
        }

        binding.actvWarned.doOnTextChanged { text, start, before, count ->
            binding.tilTimer.visibility = if (text!!.contains("Sim")) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun insertListeners() {
        binding.tilDate.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time) * -1
                binding.tilDate.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.tilHour.editText?.setOnClickListener {
            val timerPicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .build()
            timerPicker.addOnPositiveButtonClickListener {
                val minute =
                    if (timerPicker.minute in 0..9) "0${timerPicker.minute}" else timerPicker.minute

                val hour =
                    if (timerPicker.hour in 0..9) "0${timerPicker.hour}" else timerPicker.hour

                binding.tilHour.text = "${hour}:${minute}"
            }
            timerPicker.show(supportFragmentManager, null)
        }

        binding.tilTimer.editText?.setOnClickListener {
            val picker =
                MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setInputMode(INPUT_MODE_KEYBOARD)
                    .setHour(0)
                    .setMinute(0)
                    .setTitleText("Me lembrar da tarefa faltando")
                    .build()

            picker.addOnPositiveButtonClickListener {
                val minute =
                    if (picker.minute in 0..9) "0${picker.minute}" else picker.minute

                val hour =
                    if (picker.hour in 0..9) "0${picker.hour}" else picker.hour

                if (picker.hour == 0) {
                    val txt = "minuto" + if (picker.minute > 1) "s" else ""
                    binding.tilTimer.text = "$minute $txt"
                }

                if (picker.minute == 0) {
                    val txt = "hora" + if (picker.hour > 1) "s" else ""
                    binding.tilTimer.text = "$hour $txt"
                }

                if (picker.hour != 0 && picker.minute != 0) {
                    binding.tilTimer.text = "${hour}h e $minute min"
                }

                alarmHour = if (picker.hour in 0..9) "0${picker.hour}" else picker.hour.toString()
                alarmMinute = if (picker.minute in 0..9) "0${picker.minute}" else picker.minute.toString()

            }

            picker.show(supportFragmentManager, null)
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnNewTask.setOnClickListener {
            if (checkFormulario()) {

                val sdfd = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val sdfa = SimpleDateFormat("HH:mm", Locale.getDefault())

                val date = sdfd.parse("${binding.tilDate.text} ${binding.tilHour.text}")

                val timeDate = sdfa.parse(binding.tilHour.text)!!.time
                val timeAlarm = sdfa.parse("$alarmHour:$alarmMinute")!!.time

                val alarmTime = timeDate.minus(timeAlarm)
                val alarmHour = TimeUnit.MILLISECONDS.toHours(alarmTime) % 24
                val alarmMinute = TimeUnit.MILLISECONDS.toMinutes(alarmTime) % 60


                val timestampDate = (date.time.div(1000))
                val timestampAlarm = sdfd.parse("${binding.tilDate.text} $alarmHour:$alarmMinute")!!.time.div(1000)

                task = TaskResponseValue(
                    id = 0,
                    title = binding.tilTitle.text,
                    date = binding.tilDate.text,
                    hour = binding.tilHour.text,
                    description = binding.tilDescription.text,
                    task_type = binding.tilTaskType.text,
                    remember_task = binding.tilWarned.text,
                    remember_time = binding.tilTimer.text,
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

        if (binding.tilDate.text.isEmpty()) {
            binding.tilDate.error = "Defina uma data."
            return false
        }

        if (binding.tilHour.text.isEmpty()) {
            binding.tilHour.error = "Defina um horário."
            return false
        }

        if (binding.tilTaskType.text.isEmpty()) {
            binding.tilTaskType.error = "Defina um tipo de tarefa."
            return false
        }

        if (binding.tilWarned.text.isEmpty()) {
            binding.tilWarned.error = "Campo obrigatório."
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
                            startActivity(Intent(this@AddTaskActivity, ConfirmTaskActivity::class.java).putExtra(TASK_ID, task))
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