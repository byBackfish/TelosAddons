package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.item.ItemStack

open class ItemDropEvent(val item: ItemStack, val slot: Int) : Event() {

    class FromInventory(item: ItemStack, slot: Int) : ItemDropEvent(item, slot)
    class FromHotbar(item: ItemStack, slot: Int) : ItemDropEvent(item, slot)

}