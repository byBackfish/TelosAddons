package de.bybackfish.telosaddons.listeners
import de.bybackfish.telosaddons.core.event.Subscribe

import de.bybackfish.telosaddons.events.*
import de.bybackfish.telosaddons.events.telos.*
import de.bybackfish.telosaddons.telos.BagType
import de.bybackfish.telosaddons.telos.TelosBoss
import gg.essential.universal.UChat
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.Items
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
import java.util.*

class AdvancedListeners {

    @Subscribe
    fun onPacket(event: PacketEvent.Incoming) {
        if (event.packet is GameMessageS2CPacket) {
            if (ClientChatEvent.Received(event.packet.content.string).call()) event.isCancelled =
                true
        }

        if (event.packet is ChatMessageC2SPacket) {
            println("Message Event!: ${event.packet.chatMessage}")
        }

        // action bar message
        if (event.packet is GameMessageS2CPacket) {
            //     println("Action bar message: ${event.packet.content.string}")
            if (ActionBarMessageEvent(event.packet).call()) event.isCancelled = true
        }
    }

    val bossSpawnedMatcher = Regex("(\\w+) has spawned at (-?\\d+\\.\\d+), (-?\\d+\\.\\d+), (-?\\d+\\.\\d+)")
    val bossDefeatedMatcher = Regex("(\\w+) has been defeated!?")
    val nexusMatcher = Regex("discord.telosrealms.com discord.telosrealms.com")
    val bossDeathMatcher = Regex("^={47}")
    val bossDeathPlayerParticipatedMatcher = Regex("^\\d{1,3}% ?[#\\d]? ?.* (.+)$")

    var receivedBossDeathTime = 0L

    @Subscribe
    fun onChat(event: ClientChatEvent.Received) {
        val message = event.message.trim()

        if(message.matches(bossSpawnedMatcher)) {
            val match = bossSpawnedMatcher.find(message)!!
            val bossName = match.groupValues[1]
            val foundBoss = TelosBoss.fromName(bossName) ?: return
            BossSpawnEvent(foundBoss).call()

            return
        }

        if(message.matches(bossDefeatedMatcher)) {
            val match = bossDefeatedMatcher.find(message)!!
            val bossName = match.groupValues[1]
            val foundBoss = TelosBoss.fromName(bossName) ?: return
            BossDefeatedEvent(foundBoss).call()

            return
        }

        if(message.matches(nexusMatcher)) {
            JoinNexusEvent().call()
            return
        }

        if(message.matches(bossDeathMatcher)) {
            receivedBossDeathTime = System.currentTimeMillis()
            return
        }

        if(System.currentTimeMillis() - receivedBossDeathTime < 1000 && message.matches(bossDeathPlayerParticipatedMatcher)) {
            val name = bossDeathPlayerParticipatedMatcher.find(message)!!.groupValues[1]
            if(name == MinecraftClient.getInstance().player!!.name.string) {
                BossCompleteEvent(-1).call()
            }
            return
        }
    }

    val handledItems = mutableSetOf<UUID>()

    @Subscribe
    fun onItemDisplaySpawn(event: ItemDisplaySpawnEvent) {
        val itemStack = event.itemRef.get()
        if(itemStack.item != Items.CARROT_ON_A_STICK) return
        if(handledItems.contains(event.entity.uuid)) return

        val components = itemStack.components
        val customModelData = (if(components.contains(DataComponentTypes.CUSTOM_MODEL_DATA)) components.get(
            DataComponentTypes.CUSTOM_MODEL_DATA)?.value else return) ?: return

        handledItems.add(event.entity.uuid)

        val bagType = BagType.fromDroppedModelData(customModelData) ?: return

        BagDropEvent(bagType, event.itemRef, event.entity).call()
    }
}