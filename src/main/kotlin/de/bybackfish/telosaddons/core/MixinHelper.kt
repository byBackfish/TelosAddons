package de.bybackfish.telosaddons.core

import de.bybackfish.telosaddons.events.ItemDisplaySpawnEvent
import de.bybackfish.telosaddons.utils.MutableRef
import gg.essential.universal.UChat
import net.minecraft.entity.Entity
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.decoration.DisplayEntity
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.ItemStack
import java.lang.reflect.Method
import java.util.Timer
import kotlin.concurrent.schedule
import kotlin.reflect.full.declaredFunctions

var isNextBagFaked = false

fun handleEntity(reason: String, entity: Entity?) {
    Timer().schedule(100L) {
        if (entity == null) {
            return@schedule
        }
        if (entity is ItemDisplayEntity) {
            if (entity.data == null) {
                return@schedule
            }
            val itemStack: ItemStack = entity.dataTracker.get(ItemDisplayEntity.ITEM)
            val ref = MutableRef(itemStack)

            handleEntityItemStack(reason, entity, ref)

            if(ref.isDirty()) {
                entity.dataTracker.set(ItemDisplayEntity.ITEM, ref.get())
                entity.refreshData(false, 0f)
            }
        }
    }
}

fun handleEntityItemStack(reason: String, displayEntity: ItemDisplayEntity, ref: MutableRef<ItemStack>) {
    ItemDisplaySpawnEvent(reason, displayEntity, ref).call()
}