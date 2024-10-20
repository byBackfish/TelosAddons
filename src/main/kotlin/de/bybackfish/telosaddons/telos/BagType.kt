package de.bybackfish.telosaddons.telos

import net.minecraft.client.MinecraftClient
import net.minecraft.client.sound.SoundInstance
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.CustomModelDataComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

enum class BagType(val droppedModelData: Int, val totemModelData: Int = -1, val soundName: String? = null) {
    YELLOW(829116),
    ORANGE(829117),
    BROWN(829118),
    LIGHT_BROWN(829119),
    PINK(829120),
    BORDEAUX(829121),
    TURQUOISE(829122),
    CYAN(829123),
    LIGHT_BLUE(829124),
    DARK_BLUE(829125),
    RUNE(829139, 14, "rune"),
    GOLD(829142, 15, "companion"),
    GREEN(829126, 13, "irradiated"),
    WHITE(829127, 11, "royal"),
    BLACK(829128, 10, "bloodshot"),
    UNHOLY(829129, 12, "unholy"),
    HALLOWEEN(829137, 9, "halloween"),
    OTHER(-1, -1);


    companion object {
        fun fromTotemModelData(totemModelData: Int): BagType? {
            return entries.filter{
                it.totemModelData != -1
            }.firstOrNull { it.totemModelData == totemModelData }
        }

        fun fromDroppedModelData(droppedModelData: Int): BagType? {
            return entries.firstOrNull { it.droppedModelData == droppedModelData }
        }
    }

    fun createFakeDroppedItemStack(): ItemStack {
        val newItem = ItemStack(Items.CARROT_ON_A_STICK)
        newItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent(droppedModelData))
        return newItem
    }

    fun createFakeTotemItemStack(): ItemStack {
        if(totemModelData == -1) return ItemStack(Items.AIR)
        val newItem = ItemStack(Items.TOTEM_OF_UNDYING)
        newItem.set(DataComponentTypes.CUSTOM_MODEL_DATA, CustomModelDataComponent(totemModelData))
        return newItem
    }

    // noise:player.bags.<create|land|open>_<bag name>

    private fun getSoundName(type: String): String? {
        if(soundName == null) return null
        return "noise:player.bags.${type}_${soundName.lowercase()}"
    }
    fun playSounds() {
        if(soundName == null) return
        Thread {
            playSound(getSoundName("create")!!)
            Thread.sleep(1000)
            playSound(getSoundName("land")!!)
        }.start()
    }

    private fun playSound(name: String) {
        val sound = SoundEvent.of(Identifier.of(name))
        println("Playing Sound: $sound")
        MinecraftClient.getInstance().world?.playSound(MinecraftClient.getInstance().player, MinecraftClient.getInstance().player?.blockPos, sound, SoundCategory.PLAYERS, 1.0f, 1.0f)
    }
}