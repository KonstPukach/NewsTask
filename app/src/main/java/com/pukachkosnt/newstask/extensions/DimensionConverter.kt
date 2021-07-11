package com.pukachkosnt.newstask.extensions

import android.content.res.Resources
import android.util.TypedValue

fun convertToPx(dp: Float, r: Resources?): Float {
    return r?.let {
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            it.displayMetrics
        )
    } ?: dp
}