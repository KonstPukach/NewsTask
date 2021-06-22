package com.pukachkosnt.newstask.extensions

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val inputManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE)
            as InputMethodManager
    var view = this.currentFocus
    if (view == null) {
        view = View(this)
    }
    inputManager.hideSoftInputFromWindow(view.windowToken, 0)
}
