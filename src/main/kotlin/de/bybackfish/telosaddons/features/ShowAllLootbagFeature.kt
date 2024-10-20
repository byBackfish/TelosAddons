package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.Property
import de.bybackfish.telosaddons.core.event.Subscribe
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.events.ItemDisplaySpawnEvent
import de.bybackfish.telosaddons.events.PacketEvent
import de.bybackfish.telosaddons.extensions.title
import de.bybackfish.telosaddons.telos.BagType
import gg.essential.universal.UChat
import gg.essential.vigilance.data.PropertyType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.CustomModelDataComponent
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity
import net.minecraft.item.Items
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
import java.util.*
import javax.xml.crypto.Data

@Category("UI")
class ShowAllLootbagFeature: Feature() {

    @Property
    var announceInChat = true

    private val handledItems = mutableSetOf<UUID>()

    init {
        BagType.entries.forEach {
            if(it.totemModelData == -1) {
                addSetting(
                    LocalProperty(
                        name = showBagName(it),
                        default = false,
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
    fun onItemDisplaySpawn(event: ItemDisplaySpawnEvent) {
        val itemStack = event.itemStack
        if(itemStack.item != Items.CARROT_ON_A_STICK) return
        if(handledItems.contains(event.entity.uuid)) return

        val components = itemStack.components
        val customModelData = (if(components.contains(DataComponentTypes.CUSTOM_MODEL_DATA)) components.get(DataComponentTypes.CUSTOM_MODEL_DATA)?.value else return) ?: return

        handledItems.add(event.entity.uuid)

        val bagType = BagType.fromDroppedModelData(customModelData) ?: return

        if(announceInChat) UChat.chat("You dropped a $bagType bag (${event.reason}")

        if(bagType.totemModelData != -1) return
        if(!shouldShowBag(bagType)) return

        mc.gameRenderer.showFloatingItem(itemStack)
    }

}