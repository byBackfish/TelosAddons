package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event

open class ClientChatEvent(val message: String) : Event() {
    class Received(message: String) : ClientChatEvent(message) {}
    class Sent(message: String) : ClientChatEvent(message) {}
}