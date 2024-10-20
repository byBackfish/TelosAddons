package de.bybackfish.telosaddons.mixins;

import de.bybackfish.telosaddons.events.ScreenInitEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
 protected TitleScreenMixin(Text title) {
  super(title);
 }

 @Inject(method = "init", at = @At("HEAD"))
    public void init(CallbackInfo ci) {
    new ScreenInitEvent(this).call();
    }
}
