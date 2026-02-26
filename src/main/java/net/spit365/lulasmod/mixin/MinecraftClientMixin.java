package net.spit365.lulasmod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.spit365.lulasmod.custom.Shimmer;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	@Nullable
	public ClientPlayerEntity player;

	@Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
	public void hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (Shimmer.mindShimmerEnabled && player != null && entity.distanceTo(player) < 15) cir.setReturnValue(true);
	}
}
