package de.bybackfish.telosaddons.utils

import me.x150.renderer.util.RendererUtils
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

fun renderTextInWorld(context: DrawContext, pos: Vec3d, texts: List<String>, color: Int, yOffset: Int = 10) {
    val client = MinecraftClient.getInstance()
    val screenSpace = worldSpaceToScreenSpace(pos)

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

fun worldSpaceToScreenSpace(pos: Vec3d): Vec3d {
    val client = MinecraftClient.getInstance()
    val camera = client
        .entityRenderDispatcher.camera
    val displayHeight = client.window.height
    val target = Vector3f()

    val deltaX = pos.x - camera.pos.x
    val deltaY = pos.y - camera.pos.y
    val deltaZ = pos.z - camera.pos.z

    val transformedCoordinates = Vector4f(deltaX.toFloat(), deltaY.toFloat(), deltaZ.toFloat(), 1f).mul(
        RendererUtils.lastWorldSpaceMatrix
    )

    val matrixProj = Matrix4f(RendererUtils.lastProjMat)
    val matrixModel = Matrix4f(RendererUtils.lastModMat)

    matrixProj.mul(matrixModel)
        .project(
            transformedCoordinates.x(),
            transformedCoordinates.y(),
            transformedCoordinates.z(),
            RendererUtils.lastViewport,
            target
        )

    return Vec3d(
        target.x / client.window.scaleFactor,
        (displayHeight - target.y) / client.window.scaleFactor, target.z.toDouble()
    )
}