package de.bybackfish.telosaddons.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import de.bybackfish.telosaddons.events.ItemDisplaySpawnEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPacketListener;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.ModifiableWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static de.bybackfish.telosaddons.core.MixinHelperKt.handleEntity;

@Mixin(ClientPlayNetworkHandler.class)
public class EntitySpawnMixin {

    @Inject(method = "onEntitySpawn", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onSpawnPacket(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V"))
    void onEntitySpawn(EntitySpawnS2CPacket packet, CallbackInfo ci, @Local(ordinal = 0) Entity entity) {
        if(entity == null) return;
        MinecraftClient.getInstance().executeTask(() -> {
            handleEntity("spawn", entity);
        });
    }

    /*
    @Inject(method = "onEntity", at = @At("HEAD"))
    void onEntity(EntityS2CPacket packet, CallbackInfo ci) {
        Entity entity = packet.getEntity((World) ((ClientPlayNetworkHandler) (Object) this).getWorld());
        if(entity == null) return;
        handleEntity("raw", entity);
    }

    @Inject(method = "onEntityAttributes", at = @At("RETURN"))
    void onEntityAttributes(EntityAttributesS2CPacket packet, CallbackInfo ci) {
        Entity entity = ((ClientPlayNetworkHandler) (Object) this).getWorld().getEntityById(packet.getEntityId());
        handleEntity("Attribute", entity);
    }

    @Inject(method = "onEntityVelocityUpdate", at = @At("RETURN"))
    void onEntityVelocity(EntityVelocityUpdateS2CPacket packet, CallbackInfo ci) {
        Entity entity = ((ClientPlayNetworkHandler) (Object) this).getWorld().getEntityById(packet.getEntityId());
        handleEntity("velcoity", entity);
    }

    @Inject(method = "onEntityAnimation", at = @At("RETURN"))
    void onEntityAnimation(EntityAnimationS2CPacket packet, CallbackInfo ci) {
        Entity entity = ((ClientPlayNetworkHandler) (Object) this).getWorld().getEntityById(packet.getEntityId());
        handleEntity("animation", entity);
    }

    @Inject(method = "onEntityEquipmentUpdate", at = @At("RETURN"))
    void onEntityEquipmentUpdate(EntityEquipmentUpdateS2CPacket packet, CallbackInfo ci) {
        Entity entity = ((ClientPlayNetworkHandler) (Object) this).getWorld().getEntityById(packet.getEntityId());
        handleEntity("equipment", entity);
    }

    @Inject(method = "onEntityPosition", at = @At("RETURN"))
    void onEntityPosition(EntityPositionS2CPacket packet, CallbackInfo ci) {
        Entity entity = ((ClientPlayNetworkHandler) (Object) this).getWorld().getEntityById(packet.getEntityId());
        handleEntity("position", entity);
    }
     */
}
