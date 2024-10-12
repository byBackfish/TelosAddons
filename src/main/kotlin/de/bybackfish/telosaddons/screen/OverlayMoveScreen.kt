package de.bybackfish.telosaddons.screen

import com.mojang.blaze3d.systems.RenderSystem
import de.bybackfish.telosaddons.TelosAddons
import de.bybackfish.telosaddons.core.feature.FeatureManager
import de.bybackfish.telosaddons.core.feature.struct.FeatureState
import de.bybackfish.telosaddons.extensions.text
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.boss.BossBar
import java.awt.Color


class OverlayMoveScreen: Screen("Overlay Move Screen".text()) {

    private var selectedFeature: FeatureManager.OverlaySetting? = null;
    private val lastTickPosX = hashMapOf<String, Int>()
    private val lastTickPosY = hashMapOf<String, Int>()

    private fun getOverlays(): List<FeatureManager.OverlaySetting> {
        val enabledFeatures = TelosAddons.featureManager.features.values.filter { it.featureInfo.feature.state == FeatureState.ENABLED }
        val overlays = enabledFeatures.map { it.featureInfo.overlays }.flatten()
        return overlays
    }

    override fun shouldPause(): Boolean {
        return false
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, partialTicks: Float) {
        val textRenderer = MinecraftClient.getInstance().textRenderer;

        val screenWidth = context.scaledWindowWidth

        context.drawText(
            textRenderer,
            "Overlay Move Screen",
            screenWidth / 2 - textRenderer.getWidth("Overlay Move Screen") / 2,
            20,
            0xFFFFFF,
            true
        )

        val overlays = getOverlays()

        overlays.forEach { overlay ->
            val x = overlay.overlay.position.x
            val y = overlay.overlay.position.y
            val width = overlay.overlay.size.width
            val height = overlay.overlay.size.height

            val name = overlay.overlay.overlayName

            if(!lastTickPosX.containsKey(name)) {
                lastTickPosX[name] = overlay.overlay.position.x
            }

            if(!lastTickPosY.containsKey(name)) {
                lastTickPosY[name] = overlay.overlay.position.y
            }

            val actualPosX = (lastTickPosX[name]!! + ((overlay.overlay.position.x - lastTickPosX[name]!!) * partialTicks)).toInt()
            val actualPosY =
                (lastTickPosY[name]!! + (overlay.overlay.position.y - lastTickPosY[name]!!) * partialTicks).toInt()

            overlay.overlay.renderDummy(context, partialTicks)

            val borderColor = Color.YELLOW.rgb
            context.drawBorder(x-3, y-3, width + 6 , height + 6, borderColor)

            lastTickPosX[name] = actualPosX.toInt()
            lastTickPosY[name] = actualPosY.toInt()
        }
    }


    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        selectedFeature = null

        getOverlays().forEach {
            val overlay = it.overlay
            val name = it.overlay.overlayName

            val x = overlay.position.x
            val y = overlay.position.y

            val width = overlay.size.width
            val height = overlay.size.height

            if (mouseX >= x - width && mouseX <= x + width && mouseY >= y - height && mouseY <= y + height) {
                selectedFeature = it
                println("Selected: $name")
                return@forEach;
            } else {
                println("Not Selected: $name")
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    var dragOffsetX = 0.0
    var dragOffsetY = 0.0

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (selectedFeature != null) {
            val overlay = selectedFeature!!.overlay

            // Calculate offset on first drag
            if (!isDragging) {
                dragOffsetX = mouseX - overlay.position.x
                dragOffsetY = mouseY - overlay.position.y
                isDragging = true
            }

            // Update the position based on the offset
            val newX = mouseX - dragOffsetX
            val newY = mouseY - dragOffsetY
            overlay.position = overlay.position.copy(x = newX.toInt(), y = newY.toInt())
            overlay.dirty()
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        saveMovePosition(mouseX, mouseY)
        isDragging = false // Reset dragging state on release
        return super.mouseReleased(mouseX, mouseY, button)
    }

    fun saveMovePosition(mouseX: Double, mouseY: Double) {
        if (selectedFeature != null) {
            val overlay = selectedFeature!!.overlay
            val newX = mouseX - dragOffsetX
            val newY = mouseY - dragOffsetY
            overlay.position = overlay.position.copy(x = newX.toInt(), y = newY.toInt())
            overlay.dirty()
        }
    }
}