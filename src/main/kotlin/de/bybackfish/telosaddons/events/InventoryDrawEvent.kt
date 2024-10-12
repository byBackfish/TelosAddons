package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.client.gui.DrawContext

class InventoryDrawEvent(
    val matrices: DrawContext,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
) : Event()