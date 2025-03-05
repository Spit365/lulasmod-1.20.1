package net.spit365.lulasmod.mixin;

import net.minecraft.client.render.entity.feature.SkinOverlayOwner;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModEntities;
import net.spit365.lulasmod.mod.ModImportant;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity implements SkinOverlayOwner {
    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
		super(entityType, world);
	}


	@Inject(method = "explode", at = @At("HEAD"), cancellable = true)
	private void onExplode(CallbackInfo ci) {
		if (!this.getWorld().isClient && this.getType() == ModEntities.SMOKE_CREEPER) {
			ModImportant.summonSmoke(this.getPos(), this.getWorld());
			this.dead = true;
			this.discard();
			ci.cancel();
        }
	}
}
