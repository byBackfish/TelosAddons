package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket

class ActionBarMessageEvent(val packet: GameMessageS2CPacket) : Event()