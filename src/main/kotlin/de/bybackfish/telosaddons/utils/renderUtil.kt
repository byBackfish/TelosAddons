package de.bybackfish.telosaddons.utils

import gg.essential.universal.UChat
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.math.Vec3d
import org.jetbrains.annotations.ApiStatus
import org.joml.Matrix4f
import org.joml.Vector3f
import org.joml.Vector4f

@ApiStatus.Internal
val lastProjMat = Matrix4f()
@ApiStatus.Internal
val lastModMat = Matrix4f()
@ApiStatus.Internal
val lastWorldSpaceMatrix = Matrix4f()
@ApiStatus.Internal
val lastViewport = IntArray(4)

fun renderTextInWorld(context: DrawContext, pos: Vec3d, texts: List<String>, color: Int, yOffset: Int = 10, debug: Boolean = false) {
    val client = MinecraftClient.getInstance()
    val screenSpace = worldSpaceToScreenSpace(pos)

    if (isVisible(screenSpace, debug)) {
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

fun isVisible(pos: Vec3d?, debug: Boolean = false): Boolean {
    if(pos == null) return false;
    val roundedZ = (pos.z * 10000).toInt() / 10000.0
    val isVisible = roundedZ >= -1 && roundedZ <= 1
    if(debug)
        UChat.chat("$isVisible | $roundedZ")
    return isVisible
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

    val matrixProj = Matrix4f(lastProjMat)
    val matrixModel = Matrix4f(lastModMat)

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