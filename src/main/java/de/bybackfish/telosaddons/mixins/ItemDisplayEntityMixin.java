package de.bybackfish.telosaddons.mixins;

import de.bybackfish.telosaddons.events.ItemDisplaySpawnEvent;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.bybackfish.telosaddons.core.MixinHelperKt.handleEntity;
import static de.bybackfish.telosaddons.core.MixinHelperKt.handleEntityItemStack;

@Mixin(DisplayEntity.ItemDisplayEntity.class)
public class ItemDisplayEntityMixin {

    @Inject(method = "setItemStack", at = @At("HEAD"))
    public void setItemStack(ItemStack stack, CallbackInfo ci) {
        handleEntityItemStack("setItemStack", (DisplayEntity.ItemDisplayEntity) (Object) this, stack);
    }

}
