package com.pukachkosnt.newstask.model


data class Source(
    val sourceId: String,
    val name: String,
    val description: String,
    val language: String,
    val country: String,
    val isFav: Boolean = false
) : Option(
    id = sourceId,
    title = name,
    checked = isFav
)