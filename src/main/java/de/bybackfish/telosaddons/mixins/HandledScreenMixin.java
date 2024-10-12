package de.bybackfish.telosaddons.mixins;

import static com.mojang.blaze3d.systems.RenderSystem.depthMask;

import de.bybackfish.telosaddons.TelosAddons;
import de.bybackfish.telosaddons.events.DrawSlotEvent;
import de.bybackfish.telosaddons.events.GUIKeyPressEvent;
import de.bybackfish.telosaddons.events.ItemActionEvent;
import de.bybackfish.telosaddons.events.gui.ClickGuiEvent;
import de.bybackfish.telosaddons.events.gui.CloseGuiEvent;
import de.bybackfish.telosaddons.events.gui.InitGuiEvent;
import de.bybackfish.telosaddons.events.gui.RenderGuiEvent;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen {

  @Shadow
  protected int backgroundWidth;
  @Shadow
  protected int backgroundHeight;
  @Shadow
  @Nullable
  protected Slot focusedSlot;
  @Shadow
  @Final
  protected T handler;

  protected HandledScreenMixin(Text title) {
    super(title);
  }

  @Inject(
      at = @At("HEAD"),
      method = "keyPressed",
      cancellable = true
  )
  void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
    ItemStack itemClickedAt = null;
    if (this.focusedSlot != null) {
      itemClickedAt = this.focusedSlot.getStack();
    }
    if (new GUIKeyPressEvent(keyCode, scanCode, modifiers, this.focusedSlot,
        itemClickedAt).call()) {
      cir.setReturnValue(true);
    }
  }


  /**
   * @author y
   * @reason y
   */
  @Overwrite
  public void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
    if (slot != null) {
      slotId = slot.id;
    }

    if (slot != null) {
      if (new ItemActionEvent(actionType, slot, slot.getStack()).call()) {
        return;
      }
    }

    assert Objects.requireNonNull(this.client).interactionManager != null;
    assert this.client.interactionManager != null;
    this.client.interactionManager.clickSlot(this.handler.syncId, slotId, button, actionType,
        this.client.player);
  }


  @Inject(at = @At("TAIL"), method = "drawSlot", cancellable = true)
  public void drawSlotTail(DrawContext context, Slot slot, CallbackInfo ci) {
    if (slot.inventory instanceof PlayerInventory) {
      if (new DrawSlotEvent(context, slot, slot.getStack(), slot.x, slot.y).call()) {
        ci.cancel();
      }
    }
  }

  @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
  public void mouseClicked(double mouseX, double mouseY, int button,
      CallbackInfoReturnable<Boolean> cir) {
    if (new ClickGuiEvent(mouseX, mouseY, button).call()) {
      cir.setReturnValue(true);
    }
  }

  @Inject(method = "init()V", at = @At("TAIL"))
  public void init(CallbackInfo ci) {
    Screen that = (Screen) (Object) this;
    new InitGuiEvent(that).call();
  }

  @Inject(method = "render", at = @At("HEAD"), cancellable = true)
  public void drawBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
    Screen that = (Screen) (Object) this;
    if (new RenderGuiEvent(that, context, mouseX, mouseY, delta).call()) {
      ci.cancel();
    }
  }

  @Inject(method = "close", at = @At("HEAD"), cancellable = true)
  public void close(CallbackInfo ci) {
    if (new CloseGuiEvent().call()) {
      ci.cancel();
    }
  }

  @Inject(method = "drawSlot", at = @At("HEAD"))
  public void drawSlot(DrawContext context, Slot slot, CallbackInfo ci) {
    if (MinecraftClient.getInstance().currentScreen == null) {
      return;
    }
  }


}
