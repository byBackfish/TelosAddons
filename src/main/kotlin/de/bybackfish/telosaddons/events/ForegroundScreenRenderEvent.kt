package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen

class ForegroundScreenRenderEvent(
    val that: Screen,
    val matrixStack: DrawContext,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
):
    Event() {
}