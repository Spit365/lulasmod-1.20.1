package net.spit365.lulasmod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModParticles;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {super(entityType, world);}

    @Inject(method = "spawnSweepAttackParticles", at = @At("HEAD"), cancellable = true)
    private void spawnSweepAttackParticles(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getCommandTags().contains("tailed") && player.getWorld() instanceof ServerWorld) {
            double x = -MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
            double y = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
            ((ServerWorld) player.getWorld()).spawnParticles(ModParticles.SCRATCH, player.getX() + x, player.getBodyY(0.5), player.getZ() + y, 0, x, 0.0, y, 0.0);
            ci.cancel();
        }
    }
}