package net.spit365.lulasmod.mod;

import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.util.RegisterHelper;

public class ModParticles {
	public static final SimpleParticleType SCRATCH = RegisterHelper.particle("scratch", true);
	public static final SimpleParticleType GOLDEN_SHIMMER = RegisterHelper.particle("golden_shimmer", false);
	public static final SimpleParticleType CURSED_BLOOD = RegisterHelper.particle("cursed_blood", false);
	public static final SimpleParticleType EXPLOSION = RegisterHelper.particle("explosion", false);
	public static final SimpleParticleType BLOOD = (SimpleParticleType) Registries.PARTICLE_TYPE.get(Identifier.of("client-tweaks", "blood"));

	public static void init() {}
}
