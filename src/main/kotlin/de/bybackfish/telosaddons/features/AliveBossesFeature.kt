package de.bybackfish.telosaddons.features

import de.bybackfish.telosaddons.core.annotations.Category
import de.bybackfish.telosaddons.core.annotations.EnabledByDefault
import de.bybackfish.telosaddons.core.annotations.OverlayInfo
import de.bybackfish.telosaddons.core.annotations.Property
import de.bybackfish.telosaddons.core.event.Subscribe
import de.bybackfish.telosaddons.core.feature.Feature
import de.bybackfish.telosaddons.core.feature.overlay.Overlay
import de.bybackfish.telosaddons.core.feature.overlay.OverlayPosition
import de.bybackfish.telosaddons.core.feature.overlay.OverlaySize
import de.bybackfish.telosaddons.events.ChestUpdateEvent
import de.bybackfish.telosaddons.events.RenderScreenEvent
import de.bybackfish.telosaddons.events.telos.BossDefeatedEvent
import de.bybackfish.telosaddons.events.telos.BossSpawnEvent
import de.bybackfish.telosaddons.telos.TelosBoss
import de.bybackfish.telosaddons.utils.renderTextInWorld
import gg.essential.universal.UChat
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.Item
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.util.math.Vec3d
import java.awt.Color

@Category("UI")
@EnabledByDefault
class AliveBossesFeature: Feature() {

    @Property
    var showOverlay = true

    @Property
    var renderWaypoints = true

    private val aliveBosses = mutableMapOf<TelosBoss, Long>()

    val BOSS_NAME_REGEX = Regex(". \\[(.+)] .")

    @Subscribe
    fun onBossSpawn(event: BossSpawnEvent) {
        UChat.chat("Telos Boss spawned: ${event.boss.name}")
        aliveBosses[event.boss] = System.currentTimeMillis()
    }

    @Subscribe
    fun onBossDeath(event: BossDefeatedEvent) {
        if(!aliveBosses.containsKey(event.boss)) return
        UChat.chat("Telos Boss defeated: ${event.boss.name} after ${System.currentTimeMillis() - aliveBosses[event.boss]!!}ms")
        aliveBosses.remove(event.boss)
    }

    @Subscribe
    fun onGuiOpen(event: ChestUpdateEvent) {
        event.handler.slots.forEach { slot ->
            if(slot.hasStack()) {
                val name = slot.stack.name.string
                val match = BOSS_NAME_REGEX.find(name)
                if(match != null) {
                    val bossName = match.groupValues[1]
                    val boss = TelosBoss.fromName(bossName)

                    if(boss == null) {
                        UChat.chat("Unknown boss: $bossName")
                        return@forEach
                    }

                    val lore = slot.stack.getTooltip(Item.TooltipContext.DEFAULT, MinecraftClient.getInstance().player, TooltipType.BASIC)
                    if(lore.find { it.string.contains("This boss is alive") } != null) {
                        if(aliveBosses.containsKey(boss)) return@forEach
                        println("Boss is alive: $bossName")
                        aliveBosses[boss] = System.currentTimeMillis()
                        UChat.chat("Tracked alive boss: $bossName")
                        println("Boss is alive: $bossName")
                    } else {
                        if(aliveBosses.containsKey(boss)) {
                            aliveBosses.remove(boss)
                        }
                        println("Boss is dead: $bossName")
                    }
                }
            }
     }
    }

    @Subscribe
    fun onRender(event: RenderScreenEvent) {
        if(!renderWaypoints) return
        TelosBoss.entries.forEach { boss ->
            if(boss.shouldRender(aliveBosses.keys)) {
                renderBossWaypoint(event.context, boss)
            }
        }
    }

    fun lerpColor(distance: Int): Int {
        val lerp = distance / 1000.0
        val r = ((lerp * 255).coerceIn(0.0, 255.0)).toInt()
        val g = ((255 - (lerp * 255)).coerceIn(0.0, 255.0)).toInt()
        val b = 0
        return Color(r, g, b).rgb
    }

    private fun renderBossWaypoint(context: DrawContext, boss: TelosBoss) {
        val vec3d = Vec3d(boss.x.toDouble(), boss.y.toDouble(), boss.z.toDouble())
        val distance = vec3d.distanceTo(MinecraftClient.getInstance().cameraEntity?.pos)
        val color = lerpColor(distance.toInt())

        val texts = listOf(
            boss.telosName,
            "${distance.toInt()}m",
        )

        renderTextInWorld(context, vec3d, texts, color)
    }


    @OverlayInfo()
    inner class AliveBossesOverlay: Overlay(
        "Alive Bosses",
        OverlayPosition(0, 100),
        OverlaySize(100, 100)
    ) {
        override fun shouldRender(): Boolean {
            return showOverlay
        }

        override fun render(context: DrawContext, delta: Float) {
            drawBosses(context, aliveBosses)
        }

        override fun renderDummy(context: DrawContext, delta: Float) {
            drawBosses(context,
                mapOf(
                    TelosBoss.OOZUL to System.currentTimeMillis() - 65100,
                    TelosBoss.GLUMI to System.currentTimeMillis() - 5100,
                    TelosBoss.LOTIL to System.currentTimeMillis() - 100
            ))
        }

        private fun drawBosses(context: DrawContext, bosses: Map<TelosBoss, Long>) {
            if(MinecraftClient.getInstance().textRenderer == null) return


            var y = 0
            bosses.forEach { (boss, time) ->
                val timeAlive = System.currentTimeMillis() - time
                val timeAliveSeconds = timeAlive / 1000
                val timeAliveMinutes = timeAliveSeconds / 60
                val timeAliveString = when {
                    timeAliveMinutes > 0 -> "${timeAliveMinutes}m ${timeAliveSeconds % 60}s"
                    else -> "${timeAliveSeconds}s"
                }
                context.drawText(MinecraftClient.getInstance().textRenderer, "${boss.name} - $timeAliveString", position.x, position.y + y, Color.WHITE.rgb, true)
                y += 10
            }
        }

    }



}