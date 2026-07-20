package net.spit365.lulasmod.mixin;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Attackable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.mod.ModDamageTypes;
import net.spit365.lulasmod.mod.ModData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    public LivingEntityMixin(EntityType<?> entityType, Level world) {super(entityType, world);}

    @Shadow public abstract ItemStack getMainHandItem();
    @Shadow public abstract boolean isUsingItem();

    @Inject(method = "hurtServer", at = @At("HEAD"))
    private void damage(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		Entity attacker = source.getEntity();
		Holder<DamageType> typeRegistryEntry = source.typeHolder();
		if (typeRegistryEntry.is(ModDamageTypes.BLOODSUCKING) || typeRegistryEntry.is(ModDamageTypes.KINETIC_BACKLASH)) invulnerableTime = 0;
		if (attacker != null) {
            Integer i = attacker.getAttached(ModData.DAMAGE_DELAY);
            if (i != null) invulnerableTime = i;
            if (Demon.isDemon(attacker) && attacker instanceof LivingEntity demon) demon.heal(amount);
        }
    }
    @ModifyVariable(method = "travelInAir", at = @At("STORE"), ordinal = 0)
    private double travelMidAir(double d) {
        LivingEntityMixin entity = this;
        return (
            entity.getMainHandItem().getItem() instanceof BowItem &&
            entity.isUsingItem() &&
            !entity.onGround() &&
            entity.getDeltaMovement().y <= 0.0
        ) ? -0.07 : d;
    }
	@Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
	private void takeKnockback(double strength, double x, double z, CallbackInfo ci) {
		DamageSource recent = ((LivingEntity) (Object) this).getLastDamageSource();
		if (recent != null && recent.typeHolder().is(ModDamageTypes.KINETIC_BACKLASH)) ci.cancel();
	}
}