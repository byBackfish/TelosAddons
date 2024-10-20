package de.bybackfish.telosaddons.core.feature.overlay

import de.bybackfish.telosaddons.core.config.PersistentSave
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.RenderTickCounter
import java.io.Reader
import java.io.Writer

abstract class Overlay(open val overlayName: String, var position: OverlayPosition, var size: OverlaySize): PersistentSave<OverlayPosition>("overlay/$overlayName", OverlayPosition(0, 0)) {
    abstract fun render(context: DrawContext, delta: Float)
    abstract fun renderDummy(context: DrawContext, delta: Float)

    init {
        this.initPersistentSave()
    }

    open fun shouldRender(): Boolean {
        return true
    }

    override fun write(json: Json, writer: Writer) {
        println("Writing Overlay ${this.position} to file ${this.file}")
        writer.write(json.encodeToString(position))
    }

    override fun read(json: Json, data: Reader) {
        println("Reading Overlay from file ${this.file}")
        this.position = json.decodeFromString(data.readText())
    }

}


@Serializable
data class OverlayPosition(
    val x: Int,
    val y: Int
)

data class OverlaySize(
    var width: Int,
    var height: Int
)