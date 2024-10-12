package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.item.ItemStack

class ItemPickupEvent(val item: ItemStack, val slot: Int) : Event()