package de.bybackfish.telosaddons.core.commands

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource.suggestMatching
import net.minecraft.text.Text

abstract class CommandUtil {

    companion object {

    }

    fun sendFeedback(ctx: CommandContext<FabricClientCommandSource>, message: Text) {
        ctx.source.sendFeedback(message)
    }

    fun sendError(ctx: CommandContext<FabricClientCommandSource>, message: Text) {
        ctx.source.sendError(message)
    }
}