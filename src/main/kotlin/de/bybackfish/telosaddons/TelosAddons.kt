package de.bybackfish.telosaddons

import de.bybackfish.telosaddons.command.TelosAddonsCommand
import de.bybackfish.telosaddons.core.config.*
import de.bybackfish.telosaddons.core.event.EventBus
import de.bybackfish.telosaddons.core.feature.FeatureManager
import de.bybackfish.telosaddons.core.loadTranslations
import de.bybackfish.telosaddons.features.HoldToSwingFeature
import de.bybackfish.telosaddons.features.InfoOverlayFeature
import de.bybackfish.telosaddons.features.LootBagFeature
import de.bybackfish.telosaddons.listeners.AdvancedListeners
import de.bybackfish.telosaddons.listeners.ChatListener
import de.bybackfish.telosaddons.listeners.NativeListeners
import gg.essential.universal.UScreen
import gg.essential.vigilance.data.JVMAnnotationPropertyCollector
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.Screen


class TelosAddons : ModInitializer {

    companion object {
        var NAMESPACE = "telosaddons"
        var PREFIX = "§7§l[§b§lTelos§3§lAddons§7§l]§r §b> §f"


        lateinit var config: TelosConfig
        lateinit var featureManager: FeatureManager;
        lateinit var bus: EventBus;
        var command: TelosAddonsCommand = TelosAddonsCommand()

        var propertyCollector = JVMAnnotationPropertyCollector()

        public var guiToOpen: Screen? = null

        val mc: MinecraftClient
            get() = MinecraftClient.getInstance()

        lateinit var json: Json
    }

    override fun onInitialize() {
        config = TelosConfig()
        PersistentSave.loadData()

        json = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                include(serializersModule)
            }

        }

        bus = EventBus()
        featureManager = FeatureManager()

        NativeListeners().load(bus)
        bus.register(AdvancedListeners())
        bus.register(ChatListener())
        bus.register(featureManager)
        bus.register(this)

        loadTranslations()

        featureManager.register(
            LootBagFeature(),
            InfoOverlayFeature(),
            HoldToSwingFeature()
        )

        featureManager.loadToConfig()

        config.initialize()
        config.markDirty()


        ClientTickEvents.START_CLIENT_TICK.register {
            if (guiToOpen != null) {
                UScreen.displayScreen(guiToOpen)
                guiToOpen = null
            }
        }

        ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
            command.register(dispatcher)
        }

    }
}