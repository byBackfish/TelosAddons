package de.bybackfish.telosaddons.events.telos

import de.bybackfish.telosaddons.core.event.Event
import de.bybackfish.telosaddons.telos.BagType
import de.bybackfish.telosaddons.utils.MutableRef
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.ItemStack

class BagDropEvent(var bag: BagType, val itemRef: MutableRef<ItemStack>, val entity: ItemDisplayEntity): Event() {
}