package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.spit365.lulasmod.util.RegisterHelper;

public class ModDamageSources {
	public static DamageSource bloodsucking(World world) {return getDamageSource(world, BLOODSUCKING);}
	public static DamageSource amethystShard(Entity attacker) {return getDamageSource(attacker, AMETHYST_SHARD);}
	public static DamageSource needleSword(Entity attacker) {return getDamageSource(attacker, NEEDLE_SWORD);}
	public static DamageSource kineticBacklash(World world) {return getDamageSource(world, KINETIC_BACKLASH);}

	public static final RegistryKey<DamageType> BLOODSUCKING = RegisterHelper.damageType("bloodsucking");
	public static final RegistryKey<DamageType> AMETHYST_SHARD = RegisterHelper.damageType("amethyst_shard");
	public static final RegistryKey<DamageType> NEEDLE_SWORD = RegisterHelper.damageType("needle_sword");
	public static final RegistryKey<DamageType> KINETIC_BACKLASH = RegisterHelper.damageType("kinetic_backlash");

	private static DamageSource getDamageSource(Entity attacker, RegistryKey<DamageType> damageType) {
		return new DamageSource(attacker.getWorld().getRegistryManager()
			.getOrThrow(RegistryKeys.DAMAGE_TYPE)
			.getEntry(damageType.getValue())
			.orElseThrow(), attacker);
	}
	private static DamageSource getDamageSource(World world, RegistryKey<DamageType> damageType) {
		return new DamageSource(world.getRegistryManager()
			.getOrThrow(RegistryKeys.DAMAGE_TYPE)
			.getEntry(damageType.getValue())
			.orElseThrow());
	}

	public static void init() {}
}
