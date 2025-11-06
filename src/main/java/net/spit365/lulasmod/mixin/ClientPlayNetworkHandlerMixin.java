package net.spit365.lulasmod.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.spit365.lulasmod.custom.TimeForward;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"), cancellable = true)
    public void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo cir){
        if (TimeForward.Animator.isRunning()) cir.cancel();
    }
}
