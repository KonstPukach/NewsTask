package com.pukachkosnt.newstask.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val inputManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE)
            as InputMethodManager
    val view = this.currentFocus ?: View(this)
    inputManager.hideSoftInputFromWindow(view.windowToken, 0)
}
