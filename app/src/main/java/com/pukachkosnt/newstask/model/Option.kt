package com.pukachkosnt.newstask.model

abstract class Option(
    open val id: String,
    val title: String,
    open val checked: Boolean = false
)