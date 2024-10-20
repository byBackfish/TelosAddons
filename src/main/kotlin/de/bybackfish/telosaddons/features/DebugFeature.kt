package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.Keybind
import de.bybackfish.telosaddons.core.annotations.RegisterKeybinds
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.events.GUIKeyPressEvent
import gg.essential.universal.UChat
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.entity.decoration.InteractionEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import org.lwjgl.glfw.GLFW

@Category("Debug")
@RegisterKeybinds
class DebugFeature: Feature() {


    @Keybind(GLFW.GLFW_KEY_UNKNOWN)
    fun debugEntity() {
        val targetedEntity = mc.targetedEntity

        val nearbyEntities = mc.world!!.entities.filter { it is InteractionEntity || it is ItemDisplayEntity }.filter { it.distanceTo(targetedEntity) < 2 }.filter { it != targetedEntity}

        println("Found ${nearbyEntities.size} nearby entities")
        var data = "";

        for(entity in listOf(targetedEntity) + nearbyEntities) {
            if (entity != null) {
                println("Targeted Entity Name: ${entity.displayName?.string}")
                println("Targeted Entity Type: ${entity.type}")

                val tags = if (entity is ItemDisplayEntity) {
                    val item = entity.data?.itemStack ?: ItemStack.EMPTY
                    val nbt = NbtCompound()

                    val tags = item?.components?.map { it.toString() }?.toList()?.joinToString("\n") ?: "No NBT"

                    "Item Name: ${item.name.string}\nItem Type: ${item.item.name}\nNBT: \n\n$nbt\n\nTags:\n\n$tags"
                } else {
                    val nbt = NbtCompound()
                    entity.writeNbt(nbt)

                    println("Targeted Entity NBT: $nbt")
                    nbt.toString()
                }

                data += "Entity: ${entity.displayName?.string}\nType: ${entity.type?.name}\n\n$tags\n\n\n"
            } else {
                println("No targeted entity")
            }
        }


        mc.keyboard.clipboard = data
        UChat.chat("Copied NBT to clipboard")
    }

    @Keybind(GLFW.GLFW_KEY_UNKNOWN, inGUI = true)
    fun debugItem(event: GUIKeyPressEvent) {
        val item = event.itemClickedAt

        if (item != null) {
            println("Item: $item")
            item.streamTags().forEach { println(it) }

            val data = item.streamTags().map { it.toString() }.toList().joinToString("\n")
            mc.keyboard.clipboard = data
            UChat.chat("Copied NBT to clipboard")
        } else {
            println("No item")
        }
    }

}