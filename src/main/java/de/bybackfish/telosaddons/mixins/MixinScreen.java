package de.bybackfish.telosaddons.mixins;

import de.bybackfish.telosaddons.events.ForegroundScreenRenderEvent;
import de.bybackfish.telosaddons.events.RenderTooltipEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.bybackfish.telosaddons.events.ScreenInitEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    Screen that = (Screen) (Object) this;

    if (new ForegroundScreenRenderEvent(that, context, mouseX, mouseY, delta).call()) {
      ci.cancel();
    }
  }

  @Inject(
          method = "init(Lnet/minecraft/client/MinecraftClient;II)V",
          at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;init()V"))
  private void initScreen(MinecraftClient client, int width, int height, CallbackInfo info) {
    Screen screen = (Screen) (Object) this;

    new ScreenInitEvent(screen).call();
  }

}
