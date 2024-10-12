package de.bybackfish.telosaddons.extensions

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

fun PlayerEntity.findItem(min: Int, max: Int, itemType: Item): ItemStack? {
    for(i in min..max) {
        val itemStack = this.inventory.getStack(i)
        if(itemStack.item == itemType) {
            return itemStack
        }
    }
    return null
}

fun PlayerEntity.findItem(min: Int, max: Int, itemName: String): ItemStack? {
    for(i in min..max) {
        val itemStack = this.inventory.getStack(i)
        if(itemStack.name.string.raw == itemName) {
            return itemStack
        }
    }
    return null
}

fun PlayerEntity.findItemAnywhere(itemType: Item): ItemStack? {
    return this.findItem(0, 35,  itemType)
}

fun PlayerEntity.findItemAnywhere(itemName: String): ItemStack? {
    return this.findItem(0, 35,  itemName)
}