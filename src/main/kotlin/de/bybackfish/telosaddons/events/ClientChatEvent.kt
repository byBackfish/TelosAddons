package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.text.Text

open class ClientChatEvent(val message: String) : Event() {
    class Received(val text: Text, message: String) : ClientChatEvent(message) {}
    class Sent(message: String) : ClientChatEvent(message) {}
}