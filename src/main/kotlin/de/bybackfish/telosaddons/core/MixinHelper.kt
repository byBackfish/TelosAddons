package de.bybackfish.telosaddons.core

import de.bybackfish.telosaddons.events.ItemDisplaySpawnEvent
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.ItemStack
import org.spongepowered.asm.mixin.Unique

fun handleEntity(reason: String, entity: Entity?) {
    if (entity == null) {
        return
    }
    if (entity is ItemDisplayEntity) {
        if (entity.data == null) {
            return
        }
        val itemStack: ItemStack = entity.data!!.itemStack()
        handleEntityItemStack(reason, entity, itemStack)
    }
}

fun handleEntityItemStack(reason: String, displayEntity: ItemDisplayEntity, itemStack: ItemStack) {
    ItemDisplaySpawnEvent(reason, displayEntity, itemStack).call()
}