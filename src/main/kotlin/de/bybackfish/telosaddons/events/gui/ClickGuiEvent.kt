package de.bybackfish.telosaddons.events.gui

import de.bybackfish.telosaddons.core.event.Event

class ClickGuiEvent(
    val x: Double,
    val y: Double,
    val button: Int
) : Event()