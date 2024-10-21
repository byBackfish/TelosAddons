package de.bybackfish.telosaddons.utils

import net.minecraft.client.MinecraftClient

fun isInShadowlands(): Boolean {
    val biome = MinecraftClient.getInstance().world?.getBiome(MinecraftClient.getInstance().player?.blockPos)
    return biome?.key.toString().contains("rotmc:shadowlands")
}

fun isInRealm(): Boolean {
    return MinecraftClient.getInstance().world?.registryKey?.value?.path?.contains("realm2") ?: false
}