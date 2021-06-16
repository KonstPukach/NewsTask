package com.pukachkosnt.newstask.mapper

interface BaseMapper<in A, out B> {
    fun map(type: A): B
}