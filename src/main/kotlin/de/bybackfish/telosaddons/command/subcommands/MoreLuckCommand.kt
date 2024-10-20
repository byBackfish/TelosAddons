package de.bybackfish.telosaddons.command.subcommands

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import de.bybackfish.telosaddons.TelosAddons
import de.bybackfish.telosaddons.core.commands.SubCommand
import de.bybackfish.telosaddons.core.feature.struct.FeatureState
import de.bybackfish.telosaddons.features.BagFakeFeature
import de.bybackfish.telosaddons.screen.OverlayMoveScreen
import gg.essential.universal.UChat
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

class MoreLuckCommand: SubCommand("moreluck", "Gives you more luck", "/telos moreluck") {
    private val fakeBagFeature by lazy {
        TelosAddons.featureManager.getFeature<BagFakeFeature>()!!
    }

    override fun register(): LiteralArgumentBuilder<FabricClientCommandSource> {
        return LiteralArgumentBuilder.literal<FabricClientCommandSource?>("moreluck").executes {
            toggleFeature(0)
            1
        }.then(ClientCommandManager.argument("type", IntegerArgumentType.integer(0, 6)).executes {
            toggleFeature(IntegerArgumentType.getInteger(it, "type"))
            1
        })
    }

    fun toggleFeature(type: Int) {
        val nextState = if(fakeBagFeature.state == FeatureState.ENABLED) FeatureState.DISABLED else FeatureState.ENABLED
        UChat.chat("More luck is now ${nextState.name}")
        fakeBagFeature.state = nextState
        fakeBagFeature.fakeType = type
        fakeBagFeature.markDirty()
    }
}