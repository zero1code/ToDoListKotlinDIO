package com.camerax.todolist.ui

import android.content.Context
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
import com.camerax.todolist.databinding.ActivityAddTaskBinding
import com.camerax.todolist.datasource.TaskDataSource
import com.camerax.todolist.extensions.format
import com.camerax.todolist.extensions.text
import com.camerax.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import java.util.*


class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
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

            }

            picker.show(supportFragmentManager, null)
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }

        binding.btnNewTask.setOnClickListener {
            if (checkFormulario()) {
                val task = Task(
                    title = binding.tilTitle.text,
                    date = binding.tilDate.text,
                    hour = binding.tilHour.text,
                    id = intent.getIntExtra(TASK_ID, 0)
                )
                TaskDataSource.insertTask(task)

                setResult(RESULT_OK)
                finish()
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

    companion object {
        const val TASK_ID = "task_id"
    }
}