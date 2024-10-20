package de.bybackfish.telosaddons.extensions

fun String.title (): String {
    return this.substring(0, 1).uppercase() + this.substring(1).lowercase()
}