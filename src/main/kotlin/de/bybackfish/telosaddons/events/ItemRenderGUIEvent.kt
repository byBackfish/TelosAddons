package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.item.ItemStack

class ItemRenderGUIEvent(val item: ItemStack, val x: Int, val y: Int) :
    Event() {
}