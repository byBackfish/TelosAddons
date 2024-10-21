package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.EnabledByDefault
import de.bybackfish.telosaddons.core.annotations.Property
import de.bybackfish.telosaddons.core.event.Subscribe
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.events.ScreenInitEvent
import de.bybackfish.telosaddons.extensions.text
import gg.essential.universal.utils.MCMainMenuScreen
import net.minecraft.client.gui.screen.TitleScreen
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.network.ServerAddress
import net.minecraft.client.network.ServerInfo

@Category("UI")
@EnabledByDefault
class QuickJoinButton: Feature() {

    @Property
    var ip = "telosrealms.com"

    private fun getServerInfo(): ServerInfo {
        val telos = ServerInfo(
            "TelosRealms", ip, ServerInfo.ServerType.OTHER
        )
        telos.resourcePackPolicy = ServerInfo.ResourcePackPolicy.ENABLED

        return telos
    }

    @Subscribe
    fun onScreenInit(event: ScreenInitEvent) {
        if (event.screen is TitleScreen) {
            val button = ButtonWidget.builder("Join Telos".text()) {
                ConnectScreen.connect(
                    mc.currentScreen, mc, ServerAddress.parse(ip), getServerInfo(), false, null);
            }.position(event.screen.width / 2 + 104,event.screen.height / 4 + 48 + 24).size(90, 20)

            event.screen.addDrawableChild(button.build())
        }
    }

}