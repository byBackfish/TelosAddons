package de.bybackfish.telosaddons.listeners

import de.bybackfish.telosaddons.TelosAddons
import de.bybackfish.telosaddons.core.event.EventBus
import de.bybackfish.telosaddons.events.ChestOpenEvent
import de.bybackfish.telosaddons.events.ClientTickEvent
import de.bybackfish.telosaddons.events.RenderScreenEvent
import de.bybackfish.telosaddons.events.WorldRenderEvent
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen


class NativeListeners {

    var loaded = false
    fun load(bus: EventBus) {
        HudRenderCallback.EVENT.register((HudRenderCallback { context, tickDelta ->
            if (MinecraftClient.getInstance().debugHud.shouldShowDebugHud()) return@HudRenderCallback
            bus.post(RenderScreenEvent(context, tickDelta))
        }))

        ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick { client ->
            bus.post(ClientTickEvent())
            if (!loaded) {
                loaded = true
                TelosAddons.featureManager.features.forEach { it.value.postInit() }
            }
        }))

        ScreenEvents.BEFORE_INIT.register { _, screen, _, _ ->
            if (screen is GenericContainerScreen) {
                ChestOpenEvent(screen).call()
            }
        }

        WorldRenderEvents.BEFORE_ENTITIES.register {
            WorldRenderEvent.BeforeEntities(it).call()
        }


    }
}