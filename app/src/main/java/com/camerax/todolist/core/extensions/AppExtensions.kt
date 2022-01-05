package com.camerax.todolist.core.extensions

import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

private val locale = Locale("pt", "BR")

fun Date.format() : String {
    return SimpleDateFormat("E, dd 'de' MMM", locale).format(this)
}

var TextInputLayout.text : String
    get() = editText?.text.toString()
    set(value) {
        editText?.setText(value)
    }