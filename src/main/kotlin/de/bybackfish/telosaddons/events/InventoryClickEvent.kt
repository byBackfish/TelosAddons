package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event

class InventoryClickEvent(val mouseX: Double, val mouseY: Double, val button: Int) : Event()