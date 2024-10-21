package de.bybackfish.telosaddons.events.telos

import de.bybackfish.telosaddons.core.event.Event
import de.bybackfish.telosaddons.telos.TelosBoss

class BossDefeatedEvent(val boss: TelosBoss): Event() {
}