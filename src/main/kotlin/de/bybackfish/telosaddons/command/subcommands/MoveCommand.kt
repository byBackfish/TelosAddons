package de.bybackfish.telosaddons.command.subcommands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.bybackfish.telosaddons.TelosAddons
import de.bybackfish.telosaddons.core.commands.SubCommand
import de.bybackfish.telosaddons.screen.OverlayMoveScreen
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

class MoveCommand: SubCommand("move", "Move the overlay", "/telos move") {
    override fun register(): LiteralArgumentBuilder<FabricClientCommandSource> {
        return LiteralArgumentBuilder.literal<FabricClientCommandSource?>("move").executes {
            TelosAddons.guiToOpen = OverlayMoveScreen()
            1
        }
    }
}