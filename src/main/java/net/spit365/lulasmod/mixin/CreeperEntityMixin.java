package net.spit365.lulasmod.mixin;

import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModEntities;
import net.spit365.lulasmod.custom.ModImportant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity implements SkinOverlayOwner {

    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}

	@Inject(method = "explode", at = @At("HEAD"), cancellable = true)
	private void onExplode(CallbackInfo ci) {
		if (!this.getWorld().isClient) {
			if (this.getType() != ModEntities.SMOKE_CREEPER) {
				ServerWorld serverWorld = (ServerWorld) this.getWorld();
				serverWorld.spawnParticles(ParticleTypes.HEART, this.getX(), this.getY(), this.getZ(), 2, 1, 1, 1, 0);
				for (PlayerEntity player : this.getWorld().getPlayers()) {
					if (
							Objects.equals(player.getName().getString(), ModImportant.whoExplode)
							&&
							this.getPos().squaredDistanceTo(player.getPos()) <= 25
							&&
							ModImportant.creeperExplode
					) {
						player.kill();
						this.dead = true;
						this.discard();
					}
				}
			}else{
				ModImportant.summonSmoke(this.getPos(), this.getWorld());
                this.dead = true;
                this.discard();
			}
            ci.cancel();
		}
	}
}
