package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.core.annotations.Button
import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.Property
import de.bybackfish.telosaddons.core.event.Subscribe
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.events.ItemDisplaySpawnEvent
import de.bybackfish.telosaddons.events.PacketEvent
import de.bybackfish.telosaddons.events.telos.BagDropEvent
import de.bybackfish.telosaddons.extensions.title
import de.bybackfish.telosaddons.telos.BagType
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.CustomModelDataComponent
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
import java.util.*
import javax.xml.crypto.Data

@Category("UI")
class ShowAllLootbagFeature: Feature() {

    @Property(sortingOrder = 0)
    var announceInChat = true

    @Button
        (sortingOrder = 2, description = "Enable all bags", buttonText = "Enable All")
    fun enableAll() {
        BagType.entries.forEach {
            property(showBagName(it), true)
        }
    }

    @Button
        (sortingOrder = 3, description = "Disable all bags", buttonText = "Disable All")
    fun disableAll() {
        BagType.entries.forEach {
            property(showBagName(it), false)
        }
    }

    init {
        BagType.entries.forEachIndexed { index, bagType ->
            if(bagType.totemModelData == -1) {
                addSetting(
                    LocalProperty(
                        name = showBagName(bagType),
                        default = false,
                        sortingOrder = index+4,
                        type = PropertyType.SWITCH
                    )
                )
            }
        }
    }

    private fun showBagName(bagType: BagType): String {
        return "showBag${bagType.name.title()}"
    }

    private fun shouldShowBag(bagType: BagType): Boolean {
        return property<Boolean>(showBagName(bagType)) ?: false
    }

    @Subscribe
    fun onBagDrop(event: BagDropEvent) {
        println("Bag drop event | Show all lootbag feature")
        val itemStack = event.itemRef.get()
        val bagType = event.bag

        if(announceInChat) UChat.chat("You dropped a $bagType bag")

        if(bagType.totemModelData != -1) return
        if(!shouldShowBag(bagType)) return

        mc.gameRenderer.showFloatingItem(itemStack)
    }

}