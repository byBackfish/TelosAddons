package de.bybackfish.telosaddons.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import de.bybackfish.telosaddons.core.MixinHelperKt;
import de.bybackfish.telosaddons.events.telos.RareBagDropEvent;
import de.bybackfish.telosaddons.telos.BagType;
import de.bybackfish.telosaddons.utils.RenderUtilKt;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "showFloatingItem", at = @At("HEAD"))
    private void showFloatingItem(ItemStack floatingItem, CallbackInfo ci) {
        if(System.currentTimeMillis() - MixinHelperKt.getLastFakedBag() < 1000) {
            return;
        }
        if(!floatingItem.getComponents().isEmpty() && !floatingItem.getComponents().contains(DataComponentTypes.CUSTOM_MODEL_DATA)) return;
        int customModelData = Objects.requireNonNull(floatingItem.getComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA)).value();
        BagType bagType = BagType.Companion.fromTotemModelData(customModelData);
        if (bagType == null) return;
        if(bagType.getTotemModelData() == -1) return; // not a rare bag
        RareBagDropEvent rareBagDropEvent = new RareBagDropEvent(bagType);
        rareBagDropEvent.call();
    }

    @WrapOperation(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;render(Lnet/minecraft/client/render/RenderTickCounter;ZLnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/GameRenderer;Lnet/minecraft/client/render/LightmapTextureManager;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;)V"))
    void renderer_postWorldRender(WorldRenderer instance, RenderTickCounter renderTickCounter, boolean b, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, Matrix4f matrix4f2, Operation<Void> original) {
        original.call(instance, renderTickCounter, b, camera, gameRenderer, lightmapTextureManager, matrix4f, matrix4f2);

        MatrixStack matrix = new MatrixStack();
        matrix.multiplyPositionMatrix(matrix4f);

        RenderUtilKt.setLastProjectionMatrix(RenderSystem.getProjectionMatrix());
        RenderUtilKt.setLastModelMatrix(RenderSystem.getModelViewMatrix());
        RenderUtilKt.setLastProjectionMatrix(matrix.peek().getPositionMatrix());
        GL11.glGetIntegerv(GL11.GL_VIEWPORT, RenderUtilKt.getLastViewport());
    }

}
