package de.bybackfish.telosaddons.listeners

import de.bybackfish.telosaddons.core.event.Subscribe
import de.bybackfish.telosaddons.events.ClientChatEvent
import de.bybackfish.telosaddons.events.TeleportRequestEvent
import gg.essential.universal.ChatColor
import net.minecraft.client.MinecraftClient

class ChatListener {

    companion object {
        private val hideMessageRegex = mutableMapOf<Int, Regex>()
        private val hideMessageString = mutableMapOf<Int, String>()

        val ignoredPeople = listOf<String>()

        fun hide(message: String) {
            val randomId = (0..100000).random()
            hideMessageString[randomId] = message
        }

        fun hide(regex: Regex) {
            val randomId = (0..100000).random()
            hideMessageRegex[randomId] = regex
        }


        fun checkMessage(text: String): Boolean {
            val message = ChatColor.stripColorCodes(text) ?: return false
            for ((id, regex) in hideMessageRegex) {
                if (regex.matches(message)) {
                    hideMessageRegex.remove(id)
                    return true
                }
            }

            for ((id, string) in hideMessageString) {
                if (message == string) {
                    hideMessageString.remove(id)
                    return true
                }
            }

            return false
        }
    }

    @Subscribe
    fun onChat(event: ClientChatEvent.Received) {
        val strippedMessage = ChatColor.stripColorCodes(event.message) ?: return
        if (checkMessage(event.message)) event.isCancelled = true
    }

}