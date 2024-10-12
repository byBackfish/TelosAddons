package de.bybackfish.telosaddons.mixins;

import de.bybackfish.telosaddons.TelosAddons;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public abstract class MixinOptionsHud extends Screen {

  protected MixinOptionsHud(Text title) {
    super(title);
  }

  @Inject(method = "initWidgets", at = @At("HEAD"))
  private void initWidgets(CallbackInfo ci) {
    this.addDrawableChild(
        ButtonWidget.builder(Text.of("TelosAddons"), (button) -> {
          TelosAddons.Companion.setGuiToOpen(TelosAddons.Companion.getConfig().gui());
        }).position(20, 20).size(98, 20).build());
  }
}
