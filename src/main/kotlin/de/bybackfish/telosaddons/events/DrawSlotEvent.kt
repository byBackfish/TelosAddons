package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

class DrawSlotEvent(
    val matrices: DrawContext,
    val slot: Slot,
    val stack: ItemStack,
    val x: Int,
    val y: Int
) : Event()