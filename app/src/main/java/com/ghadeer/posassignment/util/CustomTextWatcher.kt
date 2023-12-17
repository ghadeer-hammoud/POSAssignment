package com.ghadeer.posassignment.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class CustomTextWatcher(
    private val editText: EditText,
    private val onEditTextChangeListener: OnEditTextChangeListener,
): TextWatcher {

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        onEditTextChangeListener.onTextChanged(this.editText)
    }

    override fun afterTextChanged(p0: Editable?) {

    }
}

interface OnEditTextChangeListener{
    fun onTextChanged(editText: EditText)
}