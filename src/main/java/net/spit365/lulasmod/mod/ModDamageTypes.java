package net.spit365.lulasmod.mod;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.util.RegisterHelper;

public final class ModDamageTypes {
	public static final ResourceKey<DamageType> BLOODSUCKING = RegisterHelper.damageType("bloodsucking");
	public static final ResourceKey<DamageType> AMETHYST_SHARD = RegisterHelper.damageType("amethyst_shard");
	public static final ResourceKey<DamageType> NEEDLE_SWORD = RegisterHelper.damageType("needle_sword");
	public static final ResourceKey<DamageType> KINETIC_BACKLASH = RegisterHelper.damageType("kinetic_backlash");

	public static DamageSource createDamageSource(Entity attacker, ResourceKey<DamageType> damageType) {
		return new DamageSource(attacker.level().registryAccess()
			.lookupOrThrow(Registries.DAMAGE_TYPE)
			.get(damageType.location())
			.orElseThrow(), attacker);
	}
	public static DamageSource createDamageSource(Level world, ResourceKey<DamageType> damageType) {
		return new DamageSource(world.registryAccess()
			.lookupOrThrow(Registries.DAMAGE_TYPE)
			.get(damageType.location())
			.orElseThrow());
	}

	public static void init() {}
}
