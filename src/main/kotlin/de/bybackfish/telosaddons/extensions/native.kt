package de.bybackfish.telosaddons.extensions

import gg.essential.universal.ChatColor
import java.util.*

val String.raw get() = ChatColor.stripColorCodes(this)
fun String.camel(): String {

    val split = this.lowercase().split("_")
    val sb = StringBuilder()
    split.forEach { split ->
        sb.append(split.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
    }
    return sb.toString()
}