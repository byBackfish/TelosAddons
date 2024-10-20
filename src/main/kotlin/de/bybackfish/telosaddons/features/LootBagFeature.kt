package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.TelosAddons
import de.bybackfish.telosaddons.TelosConfig
import de.bybackfish.telosaddons.core.annotations.Button
import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.OverlayInfo
import de.bybackfish.telosaddons.core.annotations.Property
import de.bybackfish.telosaddons.core.event.Subscribe
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.core.feature.overlay.Overlay
import de.bybackfish.telosaddons.core.feature.overlay.OverlayPosition
import de.bybackfish.telosaddons.core.feature.overlay.OverlaySize
import de.bybackfish.telosaddons.events.ClientChatEvent
import de.bybackfish.telosaddons.events.telos.*
import de.bybackfish.telosaddons.extensions.title
import de.bybackfish.telosaddons.telos.BagType
import gg.essential.vigilance.data.PropertyType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import java.awt.Color

@Category("UI")
class LootBagFeature: Feature() {

    val defaultOverlayText = "White Bags: {whiteBags}\nBlack Bags: {blackBags}\nGold Bags: {goldBags}\n\nRuns Since White: {runsSinceWhite}\nRuns Since Black: {runsSinceBlack}\nRuns Since Gold: {runsSinceGold}\nRuns Since Rune: {runsSinceRune}\n\nTotal Runs: {totalRuns}"

    @Property
    var renderLifetimeStats = true

    @Property
    var renderSessionStats = true

    @Property(
        forceType = PropertyType.PARAGRAPH,
        description = "Replaceable text: {<bag type in lowercase>Bags}, {runsSince<bag type in title>}, {totalRuns}, {otherBags}",
    )
    var overlayText = defaultOverlayText

    @Button(
        buttonText = "Reset Overlay Text",
        description = "Reset the overlayText to default",
    )
    fun resetOverlayText() {
        overlayText = defaultOverlayText
    }

    @Button(
        buttonText = "Reset Session Stats",
        description = "Reset the session stats",
    )
    fun resetSessionStats() {
        sessionBagCounts.clear()
        sessionRunsSince.clear()
        sessionTotalRuns = 0
    }

    @Property(
        hidden = true,
    )
    var totalRuns = 0

    init {
        BagType.entries.forEach {
            addSetting(
                LocalProperty(
                    name = bagName(it),
                    hidden = true,
                    default = 0,
                    type = PropertyType.NUMBER
                )
            )

            addSetting(
                LocalProperty(
                    name = sinceName(it),
                    hidden = true,
                    default = 0,
                    type = PropertyType.NUMBER
                )
            )
        }
    }

    val replaces = BagType.entries.map {
        return@map listOf(
            bagName(it) to { isLifetime: Boolean ->
                if(isLifetime) {
                    property(bagName(it)) ?: 0
                } else {
                    sessionBagCounts[it] ?: 0
                }
            },
            sinceName(it) to { isLifetime: Boolean ->
                if(isLifetime) {
                    property(sinceName(it)) ?: 0
                } else {
                    sessionRunsSince[it] ?: 0
                }
            },
        )
    }.flatten().toMap() + mapOf(
        "totalRuns" to { isLifetime: Boolean ->
            if (isLifetime) {
                totalRuns
            } else {
                sessionTotalRuns
            }
        })

    private var sessionTotalRuns = 0
    private val sessionBagCounts = mutableMapOf<BagType, Int>()
    private val sessionRunsSince = mutableMapOf<BagType, Int>()

    private fun bagName(bagType: BagType): String {
        return "${bagType.name.lowercase()}Bags"
    }

    private fun sinceName(bagType: BagType): String {
        return "runsSince${bagType.name.title()}"
    }

    private fun addOtherBag(increase: Int) {
        val current = property(bagName(BagType.OTHER)) ?: 0
        property(bagName(BagType.OTHER), current + increase)
        sessionBagCounts[BagType.OTHER] = (sessionBagCounts[BagType.OTHER] ?: 0) + increase
    }

    @Subscribe
    fun onBossComplete(event: BossCompleteEvent) {
        totalRuns++

        BagType.entries.forEach {
            val runsSince = property(sinceName(it)) ?: 0
            property(sinceName(it), runsSince + 1)

            sessionRunsSince[it] = (sessionRunsSince[it] ?: 0) + 1
        }

        sessionTotalRuns++

        addOtherBag(1)

        markDirty()
    }

    @Subscribe
    fun onBagDrop(event: RareBagDropEvent) {
        val bagType = event.bagType
        val bagName = bagName(bagType)

        val current = property(bagName) ?: 0
        property(bagName, current + 1)
        sessionBagCounts[bagType] = (sessionBagCounts[bagType] ?: 0) + 1

        sessionRunsSince[bagType] = 0
        property(sinceName(bagType), 0)

        addOtherBag(-1)

        markDirty()
    }

    @OverlayInfo()
    inner class SessionBagOverlay: BagOverlay(
        "Session Stats",
        false,
        { renderSessionStats },
        50,
    )

    @OverlayInfo()
    inner class LifetimeBagOverlay: BagOverlay(
        "Lifetime Stats",
        true,
        { renderLifetimeStats },
        200
    )

    open inner class BagOverlay(
        override val overlayName: String,
        private val isLifetime: Boolean,
        val renderCallback: () -> Boolean,
        y: Int,
    ): Overlay(
        overlayName,
        OverlayPosition(50, y),
        OverlaySize(130, 140)
    ) {

        override fun shouldRender(): Boolean {
            return renderCallback()
        }

        override fun render(context: DrawContext, delta: Float) {
            val text = getText(isLifetime)
            val height = 15 + ((text.size + 1) * 13)
            this.size.height = height

            draw(context,
                overlayName,
                text
            )
        }

        override fun renderDummy(context: DrawContext, delta: Float) {
            val text = getText(isLifetime)
            val height = 15 + ((text.size + 1) * 13)
            this.size.height = height

            draw(
                context,
                "Dummy $overlayName",
                text
            )
        }

        private fun draw(ctx: DrawContext, header: String, texts: Array<String>) {
            val x = position.x
            val y = position.y
            val height = size.height
            val width = size.width

            val borderColor = TelosAddons.config.borderColor
            val fillColor = TelosAddons.config.fillColor
            ctx.fill(x, y, x + width, y + height, fillColor.rgb)
            ctx.drawBorder(x, y, width, height, borderColor.rgb)

            ctx.drawHorizontalLine(
                x + 10,
                x + width - 10,
                y + 15,
                borderColor.rgb
            )

            val midX = x + (width / 2)
            ctx.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, header, midX, y + 5, 0xFFFFFF)

            for ((index, text) in texts.withIndex()) {
                ctx.drawCenteredTextWithShadow(MinecraftClient.getInstance().textRenderer, text, midX, y + 5 + (index*13+15), 0xFFFFFF)
            }
        }

        private fun getText(isLifetime: Boolean): Array<String> {
            val lines = overlayText.split("\n")
            return lines.map {
                var line = it
                for ((key, value) in replaces) {
                    line = line.replace("{$key}", value(isLifetime).toString())
                }
                line
            }.toTypedArray()
        }
    }
}