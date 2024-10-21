package de.bybackfish.telosaddons.mixins;

import de.bybackfish.telosaddons.events.ChestCloseEvent;
import de.bybackfish.telosaddons.events.ChestUpdateEvent;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SignItem;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GenericContainerScreenHandler.class)
public abstract class GenericContainerMixin extends ScreenHandler {


  public GenericContainerMixin(ScreenHandlerType<?> type, int syncId) {
    super(type, syncId);
  }

  @Override
  public void setStackInSlot(int slot, int revision, ItemStack stack) {
    super.setStackInSlot(slot, revision, stack);
    GenericContainerScreenHandler container = (GenericContainerScreenHandler) (Object) this;
    new ChestUpdateEvent(container).call();
  }

  @Override
  public void updateSlotStacks(int revision, List<ItemStack> stacks, ItemStack cursorStack) {
    super.updateSlotStacks(revision, stacks, cursorStack);

    GenericContainerScreenHandler container = (GenericContainerScreenHandler) (Object) this;

    new ChestUpdateEvent(container).call();
  }


  @Inject(method = "onClosed", at = @At("HEAD"))
  public void close(CallbackInfo ci) {
    GenericContainerScreenHandler container = (GenericContainerScreenHandler) (Object) this;
    new ChestCloseEvent(container).call();
  }

}
