package net.spit365.lulasmod.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.spit365.lulasmod.renderer.TimeForwardRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "handleSetTime", at = @At("HEAD"), cancellable = true)
    public void onWorldTimeUpdate(ClientboundSetTimePacket packet, CallbackInfo cir) {
        if (TimeForwardRenderer.isRunning()) cir.cancel();
    }
}
