package de.bybackfish.telosaddons.listeners
import de.bybackfish.telosaddons.core.event.Subscribe

import de.bybackfish.telosaddons.events.*
import de.bybackfish.telosaddons.events.telos.BossCompleteEvent
import de.bybackfish.telosaddons.events.telos.BossDefeatedEvent
import de.bybackfish.telosaddons.events.telos.BossSpawnEvent
import de.bybackfish.telosaddons.events.telos.JoinNexusEvent
import gg.essential.universal.UChat
import net.minecraft.client.MinecraftClient
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket

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

    val bossSpawnedMatcher = Regex("(\\w+) has spawned at (\\d+\\.\\d+), (\\d+\\.\\d+), (\\d+\\.\\d+)")
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
            BossSpawnEvent(bossName).call()

            return
        }

        if(message.matches(bossDefeatedMatcher)) {
            val match = bossDefeatedMatcher.find(message)!!
            val bossName = match.groupValues[1]
            BossDefeatedEvent(bossName).call()

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
}