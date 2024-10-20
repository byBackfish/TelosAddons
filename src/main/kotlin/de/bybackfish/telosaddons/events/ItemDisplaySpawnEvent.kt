package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.ItemStack

class ItemDisplaySpawnEvent(val reason: String, val entity: ItemDisplayEntity, val itemStack: ItemStack): Event() {


}