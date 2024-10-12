package de.bybackfish.telosaddons.mixins;

import com.mojang.authlib.GameProfile;
import de.bybackfish.telosaddons.events.ItemDropEvent;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class PlayerEntityMixin extends AbstractClientPlayerEntity {

  public PlayerEntityMixin(ClientWorld world,
      GameProfile profile) {
    super(world, profile);
  }

  @Shadow
  public abstract Hand getActiveHand();

  @Inject(
      method = "dropSelectedItem",
      at = @At("HEAD"),
      cancellable = true)
  private void dropSelectedItem(boolean dropEntireStack, CallbackInfoReturnable<ItemEntity> cir) {
    if (new ItemDropEvent.FromHotbar(this.getMainHandStack(),
        this.getInventory().selectedSlot).call()) {
      cir.setReturnValue(null);
    }
  }

}
