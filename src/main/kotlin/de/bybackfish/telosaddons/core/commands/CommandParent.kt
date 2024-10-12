package de.bybackfish.telosaddons.core.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

abstract class CommandParent(name: String) : SubCommand(name, "$name Sub Command Group", "/nem $name") {

    abstract fun getCommands(): List<SubCommand>

    override fun register(): LiteralArgumentBuilder<FabricClientCommandSource> {
        val literal = LiteralArgumentBuilder.literal<FabricClientCommandSource>(name)

        literal.executes {
            buildHelp(name).forEach { text ->
                sendFeedback(it, text)
            }
            1
        }

        for (command in getCommands()) {
            literal.then(command.register())
        }

        return literal
    }

}