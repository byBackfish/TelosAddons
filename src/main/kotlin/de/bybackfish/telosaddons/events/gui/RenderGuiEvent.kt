package de.bybackfish.telosaddons.events.gui

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen

class RenderGuiEvent(
    val screen: Screen,
    val matrices: DrawContext,
    val mouseX: Int,
    val mouseY: Int,
    val delta: Float
) : Event()