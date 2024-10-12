package de.bybackfish.telosaddons.mixins;

import de.bybackfish.telosaddons.events.InventoryClickEvent;
import de.bybackfish.telosaddons.events.InventoryDrawEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    if (new InventoryDrawEvent(context, mouseX, mouseY, delta).call()) {
      ci.cancel();
    }
  }

  @Inject(method = "mouseClicked", at = @At("RETURN"))
  public void mouseClicked(double mouseX, double mouseY, int button,
      CallbackInfoReturnable<Boolean> cir) {
    new InventoryClickEvent(mouseX, mouseY, button).call();
  }


}
