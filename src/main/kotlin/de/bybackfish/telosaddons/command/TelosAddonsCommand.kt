package de.bybackfish.telosaddons.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import de.bybackfish.telosaddons.command.subcommands.MoveCommand
import de.bybackfish.telosaddons.core.commands.CommandUtil
import de.bybackfish.telosaddons.core.commands.SubCommand
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.text.Text

class TelosAddonsCommand() : CommandUtil() {

    private val alias = listOf(
        "ta",
        "telosaddons",
        "git"
    )

    private val commands = mutableListOf<SubCommand>(
        MoveCommand(),
    )

    private fun buildHelp(): List<Text> {
        val list = mutableListOf<Text>()

        for (command in commands) {
            list.addAll(command.buildHelp())
        }

        return list
    }

    fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val literal = literal<FabricClientCommandSource>("telos")

        literal.executes {
            buildHelp().forEach { text ->
                sendFeedback(it, text)
            }
            1
        }

        for (command in commands) {
            literal.then(command.register())
        }

        val node = dispatcher.register(literal)
        alias.forEach { dispatcher.register(literal<FabricClientCommandSource>(it).redirect(node)) }
    }

    public fun addCommands(vararg commands: SubCommand) {
        for (command in commands) {
            this.commands.add(command)
        }

        println("Commands added")
    }
}