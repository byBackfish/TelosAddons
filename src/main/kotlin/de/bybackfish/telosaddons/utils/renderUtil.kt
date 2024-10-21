package de.bybackfish.telosaddons.utils

import gg.essential.universal.UChat
import me.x150.renderer.util.RendererUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Vec3d

fun renderTextInWorld(context: DrawContext, pos: Vec3d, texts: List<String>, color: Int, yOffset: Int = 10) {
    val client = MinecraftClient.getInstance()
    val screenSpace = RendererUtils.worldSpaceToScreenSpace(pos)

    // round screenspace to the 4th digit
    if (isVisible(screenSpace)) {
        texts.forEachIndexed { index, text ->
            context.drawCenteredTextWithShadow(
                client.textRenderer,
                text,
                screenSpace.x.toInt(),
                screenSpace.y.toInt() + (index * yOffset),
                color,
            )
        }
    }
}

fun isVisible(pos: Vec3d?): Boolean {
    if(pos == null) return false;
    val roundedZ = (pos.z * 10000).toInt() / 10000.0
    val isVisible = roundedZ > -1 && roundedZ < 1
    return roundedZ >= -1 && roundedZ <= 1;
}