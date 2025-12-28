package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.spit365.lulasmod.util.RegisterHelper;

public class ModDamageTypes {
	public static final RegistryKey<DamageType> BLOODSUCKING = RegisterHelper.damageType("bloodsucking");
	public static final RegistryKey<DamageType> AMETHYST_SHARD = RegisterHelper.damageType("amethyst_shard");
	public static final RegistryKey<DamageType> NEEDLE_SWORD = RegisterHelper.damageType("needle_sword");
	public static final RegistryKey<DamageType> KINETIC_BACKLASH = RegisterHelper.damageType("kinetic_backlash");

	public static DamageSource createDamageSource(Entity attacker, RegistryKey<DamageType> damageType) {
		return new DamageSource(attacker.getWorld().getRegistryManager()
			.getOrThrow(RegistryKeys.DAMAGE_TYPE)
			.getEntry(damageType.getValue())
			.orElseThrow(), attacker);
	}
	public static DamageSource createDamageSource(World world, RegistryKey<DamageType> damageType) {
		return new DamageSource(world.getRegistryManager()
			.getOrThrow(RegistryKeys.DAMAGE_TYPE)
			.getEntry(damageType.getValue())
			.orElseThrow());
	}

	public static void init() {}
}
