package de.bybackfish.telosaddons.utils

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import kotlin.math.cbrt

/*
* Taken from https://github.com/0x3C50/Renderer/blob/master/src/main/java/me/x150/renderer/util/FastMStack.java
* */
class FastMStack : MatrixStack {
    private val fEntries = ObjectArrayList<Entry>(8)
    private var top: Entry? = null

    constructor() {
        fEntries.add(Entry(Matrix4f(), Matrix3f()).also { top = it })
    }

    constructor(top4f: Matrix4f, top3f: Matrix3f) {
        fEntries.add(Entry(top4f, top3f).also { top = it })
    }

    override fun translate(x: Float, y: Float, z: Float) {
        top!!.positionMatrix.translate(x, y, z)
    }

    override fun scale(x: Float, y: Float, z: Float) {
        top!!.positionMatrix.scale(x, y, z)
        if (x == y && y == z) {
            // normal matrix is normalized, if all elements are uniform, we can just scale it based on the sign of the
            // elements. (positive / zero = no effect, negative = flip it)
            if (x < 0) {
                top!!.normalMatrix.scale(-1f)
            }
            return  // original MatrixStack implementation is missing this, resulting in invalid transformations
        }
        val inverseX = 1.0f / x
        val inverseY = 1.0f / y
        val inverseZ = 1.0f / z
        // cbrt is faster than the pure java approximation these days
        val scalar = (1f / cbrt((inverseX * inverseY * inverseZ).toDouble())).toFloat()
        top!!.normalMatrix.scale(scalar * inverseX, scalar * inverseY, scalar * inverseZ)
    }

    override fun multiply(quaternion: Quaternionf) {
        top!!.positionMatrix.rotate(quaternion)
        top!!.normalMatrix.rotate(quaternion)
    }

    override fun multiply(quaternion: Quaternionf, originX: Float, originY: Float, originZ: Float) {
        top!!.positionMatrix.rotateAround(quaternion, originX, originY, originZ)
        top!!.normalMatrix.rotate(quaternion)
    }

    override fun multiplyPositionMatrix(matrix: Matrix4f) {
        top!!.positionMatrix.mul(matrix)
    }

    override fun push() {
        fEntries.add(Entry(Matrix4f(top!!.positionMatrix), Matrix3f(top!!.normalMatrix)).also { top = it })
    }

    override fun pop() {
        check(fEntries.size != 1) { "Trying to pop an empty stack" }
        fEntries.pop()
        top = fEntries.top()
    }

    override fun peek(): MatrixStack.Entry {
        return try {
            MATRIXSTACK_ENTRY_CTOR?.invoke(top!!.positionMatrix, top!!.normalMatrix) as MatrixStack.Entry
        } catch (e: Throwable) {
            throw RuntimeException(e)
        }
    }

    override fun isEmpty(): Boolean {
        return fEntries.size == 1
    }

    override fun loadIdentity() {
        top!!.positionMatrix.identity()
        top!!.normalMatrix.identity()
    }

    @JvmRecord
    internal data class Entry(val positionMatrix: Matrix4f, val normalMatrix: Matrix3f)
    companion object {
        private var MATRIXSTACK_ENTRY_CTOR: MethodHandle? = null

        init {
            try {
                val lookup = MethodHandles.privateLookupIn(
                    MatrixStack.Entry::class.java,
                    MethodHandles.lookup()
                )
                MATRIXSTACK_ENTRY_CTOR = lookup.findConstructor(
                    MatrixStack.Entry::class.java,
                    MethodType.methodType(Void.TYPE, Matrix4f::class.java, Matrix3f::class.java)
                )
            } catch (e: IllegalAccessException) {
                throw RuntimeException(e)
            } catch (e: NoSuchMethodException) {
                throw RuntimeException(e)
            }
        }
    }
}