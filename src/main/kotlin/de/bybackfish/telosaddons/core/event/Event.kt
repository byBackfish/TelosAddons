package de.bybackfish.telosaddons.core.event

import de.bybackfish.telosaddons.TelosAddons

abstract class Event {
    var isCancelled: Boolean = false

    fun call(): Boolean {
        return TelosAddons.bus.post(this)
    }
}