package de.bybackfish.telosaddons.events


import de.bybackfish.telosaddons.core.event.Event
import de.bybackfish.telosaddons.listeners.ChatListener
import net.minecraft.client.MinecraftClient

open class TeleportRequestEvent(val playerName: String) : Event() {

    class Incoming(playerName: String) : TeleportRequestEvent(playerName) {

        fun hideAll() {
            isCancelled = true
            ChatListener.hide("(.+) has requested to teleport to you.+?".toRegex())
            ChatListener.hide("To teleport, type /tpaccept.")
            ChatListener.hide("To deny this request, type /tpdeny.")
            ChatListener.hide("This request will timeout after 120 seconds.")
        }
    }

    class Outgoing(playerName: String) : TeleportRequestEvent(playerName) {

    }

    fun accept() {
        MinecraftClient.getInstance().player!!.networkHandler.sendCommand("tpyes")
        ChatListener.hide("Teleport request accepted.")
    }

}