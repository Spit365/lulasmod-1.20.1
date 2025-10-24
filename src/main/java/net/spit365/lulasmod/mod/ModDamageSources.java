package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.spit365.lulasmod.manager.RegisterHelper;

public class ModDamageSources {
	public static DamageSource bloodsucking(Entity attacker) {return getDamageSource(attacker, BLOODSUCKING);}
	public static DamageSource amethystShard(Entity attacker) {return getDamageSource(attacker, AMETHYST_SHARD);}
	public static DamageSource needleSword(Entity attacker) {return getDamageSource(attacker, NEEDLE_SWORD);}

	public static final RegistryKey<DamageType> BLOODSUCKING = RegisterHelper.damageType("bloodsucking");
	public static final RegistryKey<DamageType> AMETHYST_SHARD = RegisterHelper.damageType("amethyst_shard");
	public static final RegistryKey<DamageType> NEEDLE_SWORD = RegisterHelper.damageType("needle_sword");

	private static DamageSource getDamageSource(Entity attacker, RegistryKey<DamageType> damageType) {
		return new DamageSource(attacker.getWorld().getRegistryManager()
			.getOrThrow(RegistryKeys.DAMAGE_TYPE)
			.getEntry(damageType.getValue())
			.orElseThrow(), attacker);
	}

	public static void init() {}
}
