package com.pukachkosnt.domain.mapper

interface BaseMapper<in A, out B> {
    fun map(type: A): B
}