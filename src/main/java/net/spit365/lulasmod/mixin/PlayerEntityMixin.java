package net.spit365.lulasmod.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.custom.Shimmer;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {super(entityType, world);}

    @Inject(method = "doSweepAttack", at = @At("HEAD"), cancellable = true)
    private void spawnSweepAttackParticles(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        if (Demon.isDemon(player) && player.level() instanceof ServerLevel serverWorld) {
            double x = -Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
            double y = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
            serverWorld.sendParticles(ModParticles.SCRATCH, player.getX() + x, player.getY(0.5), player.getZ() + y, 0, x, 0.0, y, 0.0);
            ci.cancel();
        }
    }

    @Inject(method = "isHurt", at = @At("HEAD"), cancellable = true)
    private void canFoodHeal(CallbackInfoReturnable<Boolean> cir) {
        if (Demon.isDemon(this)) cir.setReturnValue(false);
    }

    @Inject(method = "hasCorrectToolForDrops", at = @At("HEAD"), cancellable = true)
    private void canHarvest(CallbackInfoReturnable<Boolean> cir) {
        Shimmer.Variant variant = this.getAttached(ModData.APPLIED_SHIMMER_VARIANT);
        if (variant == Shimmer.Variant.FORTITUDE) cir.setReturnValue(true);
    }
}