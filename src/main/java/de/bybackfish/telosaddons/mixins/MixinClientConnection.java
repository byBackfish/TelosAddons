package de.bybackfish.telosaddons.mixins;

import de.bybackfish.telosaddons.events.PacketEvent;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

  @Shadow
  private Channel channel;

  @Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/packet/Packet;)V", cancellable = true)
  private void onSendPacketHead(Packet<?> packet, CallbackInfo info) {
    if (new PacketEvent.Outgoing(packet).call()) {
      info.cancel();
    }
  }

  @Inject(at = @At("HEAD"), method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V", cancellable = true)
  private void onReceivePacketHead(ChannelHandlerContext channelHandlerContext, Packet<?> packet,
      CallbackInfo ci) {
    if (channel.isOpen() && packet != null) {
      if (new PacketEvent.Incoming(packet).call()) {
        ci.cancel();
      }
    }
  }
}
