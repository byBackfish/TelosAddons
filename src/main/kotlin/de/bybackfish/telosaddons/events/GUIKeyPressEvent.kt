package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class GUIKeyPressEvent(
    val key: Int,
    val scanCode: Int,
    val modifiers: Int,
    val slot: Slot?,
    val itemClickedAt: ItemStack?
) : Event()