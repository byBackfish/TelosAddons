package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.network.packet.Packet

open class PacketEvent(val packet: Packet<*>) : Event() {
    class Outgoing(packet: Packet<*>) : PacketEvent(packet) {}
    class Incoming(packet: Packet<*>) : PacketEvent(packet) {}
}