package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType

class ItemActionEvent(
    val action: SlotActionType,
    val slot: Slot,
    val item: ItemStack
) : Event()