package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen

class ChestOpenEvent(val container: GenericContainerScreen) : Event()