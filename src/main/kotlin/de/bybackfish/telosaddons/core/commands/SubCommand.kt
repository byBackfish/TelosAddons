package de.bybackfish.telosaddons.core.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

abstract class SubCommand
    (
    name: String,
    description: String,
    usage: String,
) : Command(name, description, usage) {

    abstract fun register(): LiteralArgumentBuilder<FabricClientCommandSource>
}