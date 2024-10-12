package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter

class RenderScreenEvent(val context: DrawContext, val delta: RenderTickCounter) : Event() {
}