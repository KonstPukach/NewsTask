package com.pukachkosnt.newstask.utils

/**
 * Return t - on true, f - on false
 */
fun <T> Boolean.either(t: T, f: T): T = if (this) t else f

/**
 * Invoke t() on true, f() - on false
 */
fun <T> Boolean.doEither(t: () -> T, f: () -> T): T = if (this) t() else f()
