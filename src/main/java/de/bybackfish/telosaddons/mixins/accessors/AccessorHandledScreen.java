package de.bybackfish.telosaddons.mixins.accessors;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HandledScreen.class)
public interface AccessorHandledScreen {

  @Invoker("isPointOverSlot")
  boolean isPointOver(Slot slot, double pointX, double pointY);


}
