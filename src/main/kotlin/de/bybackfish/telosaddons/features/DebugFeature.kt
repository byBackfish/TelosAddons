package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.Keybind
import de.bybackfish.telosaddons.core.annotations.RegisterKeybinds
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.events.GUIKeyPressEvent
import gg.essential.universal.UChat
import net.minecraft.entity.LivingEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import org.lwjgl.glfw.GLFW

@Category("Debug")
@RegisterKeybinds
class DebugFeature: Feature() {


    @Keybind(GLFW.GLFW_KEY_UNKNOWN)
    fun debugEntity() {
        val targetedEntity = mc.targetedEntity
        if (targetedEntity != null) {
            println("Targeted Entity: $targetedEntity")
            println("Targeted Entity UUID: ${targetedEntity.uuid}")
            val nbt = NbtCompound()
            targetedEntity.writeNbt(nbt)

            println("Targeted Entity NBT: $nbt")
            mc.keyboard.clipboard = nbt.toString()

            UChat.chat("Copied NBT to clipboard")
        } else {
            println("No targeted entity")
        }
    }

    @Keybind(GLFW.GLFW_KEY_UNKNOWN)
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