package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import net.spit365.lulasmod.manager.RegisterHelper;

public class ModDamageSources {
	public static DamageSource BLOODSUCKING(Entity attacker) {return getDamageSource(attacker.getWorld(), BLOODSUCKING);}
	public static DamageSource AMETHYST_SHARD(Entity attacker) {return getDamageSource(attacker.getWorld(), AMETHYST_SHARD);}

	public static final RegistryKey<DamageType> BLOODSUCKING = RegisterHelper.damageType("bloodsucking");
	public static final RegistryKey<DamageType> AMETHYST_SHARD = RegisterHelper.damageType("amethyst_shard");

	private static DamageSource getDamageSource(World world, RegistryKey<DamageType> damageType) {
		return new DamageSource(world.getRegistryManager()
			.getOrThrow(RegistryKeys.DAMAGE_TYPE)
			.getEntry(damageType.getValue())
			.orElseThrow());
	}

	public static void init() {}
}
