package de.bybackfish.telosaddons.mixins;

import de.bybackfish.telosaddons.events.telos.RareBagDropEvent;
import de.bybackfish.telosaddons.telos.BagType;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "showFloatingItem", at = @At("HEAD"))
    private void showFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
        if(!floatingItem.getComponents().isEmpty() && !floatingItem.getComponents().contains(DataComponentTypes.CUSTOM_MODEL_DATA)) return;
        int customModelData = Objects.requireNonNull(floatingItem.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA)).value();
        BagType bagType = BagType.Companion.fromTotemModelData(customModelData);
        if (bagType == null) return;
        RareBagDropEvent rareBagDropEvent = new RareBagDropEvent(bagType);
        rareBagDropEvent.call();
    }

}
