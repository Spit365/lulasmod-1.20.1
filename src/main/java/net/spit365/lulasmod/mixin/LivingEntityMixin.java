package net.spit365.lulasmod.mixin;

import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.mod.ModDamageTypes;
import net.spit365.lulasmod.mod.ModData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    public LivingEntityMixin(EntityType<?> entityType, World world) {super(entityType, world);}

    @Inject(method = "damage", at = @At("HEAD"))
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		Entity attacker = source.getAttacker();
		RegistryEntry<DamageType> typeRegistryEntry = source.getTypeRegistryEntry();
		if (typeRegistryEntry.matchesKey(ModDamageTypes.BLOODSUCKING) || typeRegistryEntry.matchesKey(ModDamageTypes.KINETIC_BACKLASH)) timeUntilRegen = 0;
		if (attacker != null) {
            Integer i = attacker.getAttached(ModData.DAMAGE_DELAY);
            if (i != null) timeUntilRegen = i;
            if (Demon.isDemon(attacker) && attacker instanceof LivingEntity demon) demon.heal(amount);
        }
    }
	@Inject(method = "takeKnockback", at = @At("HEAD"), cancellable = true)
	private void takeKnockback(double strength, double x, double z, CallbackInfo ci) {
		DamageSource recent = ((LivingEntity) (Object) this).getRecentDamageSource();
		if (recent != null && recent.getTypeRegistryEntry().matchesKey(ModDamageTypes.KINETIC_BACKLASH)) ci.cancel();
	}
}