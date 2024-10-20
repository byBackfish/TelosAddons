package de.bybackfish.telosaddons.utils

class MutableRef<T>(private var element: T) {
    private var dirty = false

    fun set(value: T) {
        dirty = true
        element = value
    }

    fun get(): T {
        return element
    }

    fun isDirty(): Boolean {
        return dirty
    }

}