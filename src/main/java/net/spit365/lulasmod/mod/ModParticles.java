package net.spit365.lulasmod.mod;

import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.util.RegisterHelper;
import org.jetbrains.annotations.Nullable;

public final class ModParticles {
	public static final SimpleParticleType SCRATCH = RegisterHelper.particle("scratch", true);
	public static final SimpleParticleType GOLDEN_SHIMMER = RegisterHelper.particle("golden_shimmer", false);
	public static final SimpleParticleType CURSED_BLOOD = RegisterHelper.particle("cursed_blood", false);
	public static final SimpleParticleType EXPLOSION = RegisterHelper.particle("explosion", false);
	private static SimpleParticleType BLOOD = getBlood();

	public static @Nullable SimpleParticleType getBlood() {
		if (BLOOD != null) return BLOOD;
		return BLOOD = (SimpleParticleType) Registries.PARTICLE_TYPE.get(Identifier.of("client-tweaks", "blood"));
	}

	public static void init() {}
}
