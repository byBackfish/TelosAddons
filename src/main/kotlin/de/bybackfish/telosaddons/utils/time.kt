package de.bybackfish.telosaddons.utils

import java.util.*

fun formatRelativeFutureTime(then: Date): String {
    val now = Date()
    val diff = then.time - now.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    val weeks = days / 7
    val months = days / 30
    val years = days / 365

    // return it like this:
    // in 14h 13m 10s
    // in 4h 13m 12s
    // 15m 12s
    // 17s

    return when {
        years > 0 -> "in ${years}y ${months % 12}m ${days % 30}d"
        months > 0 -> "in ${months}m ${days % 30}d ${hours % 24}h"
        weeks > 0 -> "in ${weeks}w ${days % 7}d ${hours % 24}h"
        days > 0 -> "in ${days}d ${hours % 24}h ${minutes % 60}m"
        hours > 0 -> "in ${hours}h ${minutes % 60}m ${seconds % 60}s"
        minutes > 0 -> "in ${minutes}m ${seconds % 60}s"
        else -> "in ${seconds}s"
    }
}

fun hours(hours: Number): Long {
    return minutes(hours * 60)
}


fun minutes(minutes: Number): Long {
    return seconds(minutes * 60)
}

fun seconds(seconds: Number): Long {
    return ms(seconds * 1000)
}

fun ms(milliseconds: Number): Long {
    return milliseconds.toLong()
}


private operator fun Number.times(i: Int): Number {
    return this.toLong() * i
}