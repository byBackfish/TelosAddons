package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import de.bybackfish.telosaddons.utils.MutableRef
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.ItemStack

class ItemDisplaySpawnEvent(val reason: String, val entity: ItemDisplayEntity, val itemRef: MutableRef<ItemStack>): Event() {


}