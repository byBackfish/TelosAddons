package de.bybackfish.telosaddons.utils

import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

var lastProjectionMatrix = Matrix4f()
var lastModelMatrix = Matrix4f()
var lastWorldSpaceMatrix = Matrix4f()
var lastViewport = IntArray(4)


fun renderTextInWorld(context: DrawContext, pos: Vec3d, texts: List<String>, color: Int, yOffset: Int = 10) {
    val client = MinecraftClient.getInstance()
    val screenSpace = worldSpaceToScreenSpace(pos)

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
        lastWorldSpaceMatrix
    )

    val matrixProj = Matrix4f(lastProjectionMatrix)
    val matrixModel = Matrix4f(lastModelMatrix)

    matrixProj.mul(matrixModel)
        .project(
            transformedCoordinates.x(),
            transformedCoordinates.y(),
            transformedCoordinates.z(),
            lastViewport,
            target
        )

    return Vec3d(
        target.x / client.window.scaleFactor,
        (displayHeight - target.y) / client.window.scaleFactor, target.z.toDouble()
    )
}