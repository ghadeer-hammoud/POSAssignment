package com.ghadeer.posassignment.util

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ghadeer.posassignment.ui.activities.BaseActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}


fun View.showIf(condition: Boolean) {
     when (condition) {
        true -> show()
        false -> hide()
    }
}

fun getDate(): String {
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    return sdf.format(cal.time)
}

fun String.formatDate(
    baseFormat: String = "yyyy-MM-dd HH:mm:ss",
    newFormat: String = "dd MMM yyyy hh:mm a"
): String {
    return try {
        val baseDateFormat = SimpleDateFormat(baseFormat, Locale.US)
        val date = baseDateFormat.parse(this)
        val newDateFormat = SimpleDateFormat(newFormat, Locale.US)
        newDateFormat.format(date)
    } catch (e: Exception) {
        this
    }
}

fun Date.asString(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val sdf = SimpleDateFormat(format, Locale.US)
    return sdf.format(this)
}

fun Double.asMoney(showCurrency: Boolean = true): String {

    val currencyCode = "AED"
    val format = NumberFormat.getCurrencyInstance()
    format.maximumFractionDigits = 2
    format.currency = Currency.getInstance(currencyCode)
    return format.format(this).replace(currencyCode, "")
        .prependIndent(if (showCurrency) "$currencyCode " else "")
}

fun Activity.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}

fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this.requireContext(), message, length).show()
}

fun Activity.showErrorDialog(
    title: String,
    message: String,
    posBtn: String? = null,
    negBtn: String? = null,
    posAction: () -> Unit = {},
    negAction: () -> Unit = {}
) {
    val errorDialog = MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)


    if (posBtn != null) {
        errorDialog.setPositiveButton(posBtn) { dialog, which ->
            posAction()
        }
    }
    if (negBtn != null) {
        errorDialog.setNegativeButton(negBtn) { dialog, which ->
            negAction()
        }
    }
    errorDialog.show()
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.hideKeyboard() {
    view?.let { requireActivity().hideKeyboard(it) }
}

fun Editable?.castToString(): String {
    return when (this?.toString()?.isEmpty() ?: true) {
        true -> ""
        false -> this.toString().trim()
    }
}

fun Editable?.castToDouble(): Double {
    return when (this?.toString()?.isEmpty() ?: true) {
        true -> 0.0
        false -> {
            try {
                this?.toString()?.toDouble() ?: 0.0
            } catch (e: Exception) {
                0.0
            }
        }
    }
}


fun Activity.setToolbarTitle(title: String) {
    if (this is BaseActivity)
        this.setToolbarTitle(title)
}

fun Fragment.setToolbarTitle(title: String) {
    requireActivity().setToolbarTitle(title)
}