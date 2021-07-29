package com.pukachkosnt.newstask.extensions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private const val MILLS_IN_HOUR = 3600000
private val sdf = SimpleDateFormat("MMM d',' h:mm a", Locale.UK)

fun Date.toBeautifulLocalizedFormat(location: String, months: Array<String>): String {
    val offset = TimeZone.getDefault().getOffset(this.time)
    return when (location.lowercase()) {
        "ru" -> {
            val str = DateFormat.getInstance().format(this)
            val arr = str.split(".", " ", ":")
            "${arr[0]} ${months[arr[1].toInt() - 1]}, ${arr[3].toLong() + offset / MILLS_IN_HOUR}:${arr[4]}"
        }
        else -> {
            sdf.format(Date(this.time + offset))
        }
    }
}