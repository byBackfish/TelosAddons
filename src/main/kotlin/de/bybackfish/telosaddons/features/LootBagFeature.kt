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
import de.bybackfish.telosaddons.telos.BagType
import gg.essential.vigilance.data.PropertyType
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import java.awt.Color

@Category("UI")
class LootBagFeature: Feature() {

    val defaultOverlayText = "White Bags: {whiteBags}\nBlack Bags: {blackBags}\nGold Bags: {goldBags}\n\nRuns Since White: {runsSinceWhite}\nRuns Since Black: {runsSinceBlack}\nRuns Since Gold: {runsSinceGold}\n\nTotal Runs: {totalRuns}"

    @Property
    var renderLifetimeStats = true

    @Property
    var renderSessionStats = true

    @Property(
        forceType = PropertyType.PARAGRAPH,
        description = "Replaceable text: {whiteBags}, {blackBags}, {goldBags}, {otherBags}, {totalRuns}, {runsSinceWhite}, {runsSinceBlack}, {runsSinceGold}",
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
        sessionRunsSinceWhite = 0
        sessionRunsSinceBlack = 0
        sessionRunsSinceGold = 0
        sessionTotalRuns = 0
    }

    @Property(
        hidden = true,
    )
    var runsSinceWhite = 0

    @Property(
        hidden = true,
    )
    var runsSinceBlack = 0

    @Property(
        hidden = true,
    )
    var runsSinceGold = 0

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
        }
    }

    /*
    *                 "White Bags: $whiteBags",
                "Black Bags: $blackBags",
                "Gold Bags: $goldBags",
                "",
                "Runs Since White: $runsSinceWhite",
                "Runs Since Black: $runsSinceBlack",
                "Runs Since Gold: $runsSinceGold",
                "",
                "Total Runs: $totalRuns",
    *
    * */

    val replaces = mutableMapOf<String, (Boolean) -> Int>(
        "otherBags" to {
          if(it) {
              property(bagName(BagType.OTHER)) ?: 0
          } else {
                sessionBagCounts[BagType.OTHER] ?: 0
          }
        },
        "whiteBags" to {
            if(it) {
                property(bagName(BagType.WHITE)) ?: 0
            } else {
                sessionBagCounts[BagType.WHITE] ?: 0
            }
        },
        "blackBags" to {
            if(it) {
                property(bagName(BagType.BLACK)) ?: 0
            } else {
                sessionBagCounts[BagType.BLACK] ?: 0
            }
        },
        "goldBags" to {
            if(it) {
                property(bagName(BagType.GOLD)) ?: 0
            } else {
                sessionBagCounts[BagType.GOLD] ?: 0
            }
        },
        "totalRuns" to {
            if(it) {
                totalRuns
            } else {
                sessionTotalRuns
            }
        },
        "runsSinceWhite" to {
            if(it) {
                runsSinceWhite
            } else {
                sessionRunsSinceWhite
            }
        },
        "runsSinceBlack" to {
            if(it) {
                runsSinceBlack
            } else {
                sessionRunsSinceBlack
            }
        },
        "runsSinceGold" to {
            if(it) {
                runsSinceGold
            } else {
                sessionRunsSinceGold
            }
        }
    )

    private var sessionRunsSinceWhite = 0
    private var sessionRunsSinceBlack = 0
    private var sessionRunsSinceGold = 0
    private var sessionTotalRuns = 0
    private val sessionBagCounts = mutableMapOf<BagType, Int>()

    private fun bagName(bagType: BagType): String {
        return "${bagType.name.lowercase()}Bags"
    }

    fun addOtherBag(increase: Int) {
        val current = property(bagName(BagType.OTHER)) ?: 0
        property(bagName(BagType.OTHER), current + increase)
        sessionBagCounts[BagType.OTHER] = (sessionBagCounts[BagType.OTHER] ?: 0) + increase
    }

    @Subscribe
    fun onBossComplete(event: BossCompleteEvent) {
        totalRuns++
        runsSinceWhite++
        runsSinceBlack++
        runsSinceGold++

        sessionTotalRuns++
        sessionRunsSinceWhite++
        sessionRunsSinceBlack++
        sessionRunsSinceGold++

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

        when(bagType) {
            BagType.WHITE -> {
                runsSinceWhite = 0
                sessionRunsSinceWhite = 0
            }
            BagType.BLACK -> {
                runsSinceBlack = 0
                sessionRunsSinceBlack = 0
            }
            BagType.GOLD -> {
                runsSinceGold = 0
                sessionRunsSinceGold = 0
            }
            else -> {}
        }

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
            draw(context,
                overlayName,
                getText(isLifetime)
            )
        }

        override fun renderDummy(context: DrawContext, delta: Float) {
            draw(
                context,
                "Dummy $overlayName",
                getText(isLifetime)
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