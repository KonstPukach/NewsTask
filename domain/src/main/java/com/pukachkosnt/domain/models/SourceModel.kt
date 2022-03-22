package com.pukachkosnt.domain.models

data class SourceModel(
    val id: String,
    val name: String,
    val description: String,
    val language: String,
    val country: String,
    val checked: Boolean = false
)