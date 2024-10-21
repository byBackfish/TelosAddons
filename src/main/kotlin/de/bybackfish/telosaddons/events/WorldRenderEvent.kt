package de.bybackfish.telosaddons.events

import de.bybackfish.telosaddons.core.event.Event
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext

abstract class WorldRenderEvent: Event() {
    class BeforeEntities(val context: WorldRenderContext) : WorldRenderEvent() {}

}