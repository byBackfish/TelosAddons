package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.OverlayInfo
import de.bybackfish.telosaddons.core.annotations.Property
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.core.feature.overlay.Overlay
import de.bybackfish.telosaddons.core.feature.overlay.OverlayPosition
import de.bybackfish.telosaddons.core.feature.overlay.OverlaySize
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.PlayerListEntry
import net.minecraft.util.Formatting
import java.awt.Color
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Category("UI")
class InfoOverlayFeature: Feature() {

    @Property
    var showPing = true

    @Property
    var showFps = true

    fun getLineFromTablist(regex: Regex): String? {
        if (getTablist().isEmpty()) return null

        return getTablist().stream()
            .filter { player: String? ->
                regex.matches(player ?: "")
            }.findFirst().getOrNull()
    }
    private fun getTablist(): List<String> {
        val networkHandler: ClientPlayNetworkHandler = mc.networkHandler ?: return listOf()
        val playerCollection = networkHandler.playerList
        val playerListEntries = playerCollection.toList()

        if (playerListEntries.isEmpty()) return listOf()

        val playerNameList = playerListEntries.stream()
            .filter { player: PlayerListEntry -> Objects.nonNull(player.displayName) }
            .map { player: PlayerListEntry ->
                player.displayName!!
                    .string
            }
            .filter { obj: String? -> Objects.nonNull(obj) }
            .map { playerString: String ->
                stripColors(
                    playerString
                ).trim { it <= ' ' }
            }
            .filter { playerString: String -> playerString.isNotEmpty() }
            .toList()

        return playerNameList
    }
    private fun stripColors(input: String): String {
        return Formatting.strip(input) ?: input
    }


    @OverlayInfo
    inner class PingOverlay: Overlay("Ping Overlay", OverlayPosition(0, 0), OverlaySize(40, 12)) {
        private val pingMatcher = Regex("Ping: (\\d+)")

        override fun shouldRender(): Boolean {
            return showPing
        }

        override fun render(context: DrawContext, delta: Float) {
            val ping = getLineFromTablist(pingMatcher)
                ?.let { pingMatcher.find(it)?.groups?.get(1)?.value?.toIntOrNull() }
                ?: -1

            drawPing(context, ping)
        }

        override fun renderDummy(context: DrawContext, delta: Float) {
            drawPing(context, 116)
        }

        fun drawPing(ctx: DrawContext, ping: Int) {
            val color = when {
                ping < 100 -> Formatting.GREEN
                ping < 200 -> Formatting.YELLOW
                else -> Formatting.RED
            }.colorValue ?: Color.WHITE.rgb

            ctx.drawText(mc.textRenderer, "Ping: $ping", position.x, position.y, color, true)
        }
    }

    @OverlayInfo
    inner class FpsOverlay: Overlay("Fps Overlay", OverlayPosition(0, 15), OverlaySize(40, 12)) {
        private val pingMatcher = Regex("Ping: (\\d+)")

        override fun shouldRender(): Boolean {
            return showFps
        }

        override fun render(context: DrawContext, delta: Float) {
            drawFps(context, mc.currentFps)
        }

        override fun renderDummy(context: DrawContext, delta: Float) {
            drawFps(context, 256)
        }

        fun drawFps(ctx: DrawContext, ping: Int) {
            val color = when {
                ping < 60 -> Formatting.RED
                ping < 143 -> Formatting.YELLOW
                else -> Formatting.GREEN
            }.colorValue ?: Color.WHITE.rgb

            ctx.drawText(mc.textRenderer, "FPS: $ping", position.x, position.y, color, true)
        }
    }

}