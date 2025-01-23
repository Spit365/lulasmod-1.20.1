package net.spit365.lulasmod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    @Shadow public abstract Text getDisplayName();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "spawnSweepAttackParticles",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;spawnParticles(Lnet/minecraft/particle/ParticleEffect;DDDIDDDD)I"),
            cancellable = true)
    private void spawnSweepAttackParticles(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this; // Casting 'this' to PlayerEntity
        if (player.getWorld() instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) player.getWorld();
            double d = -MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
            double e = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
            boolean isTailed = false;
            for (int i = 1; i <= player.getCommandTags().size(); i++){
                if (Objects.equals(player.getCommandTags().stream().toList().get(i - 1), "tailed")){isTailed = true;}
            }
            if (isTailed){
                serverWorld.spawnParticles(Lulasmod.SCRATCH,player.getX() + d,player.getBodyY(0.5),player.getZ() + e,0,d,0.0,e, 0.0);
            }else{serverWorld.spawnParticles(ParticleTypes.SWEEP_ATTACK,player.getX() + d,player.getBodyY(0.5),player.getZ() + e,0,d,0.0,e, 0.0);}
            ci.cancel();
        }
    }
}


