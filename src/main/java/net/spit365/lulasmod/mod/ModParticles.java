package net.spit365.lulasmod.mod;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
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
		return BLOOD = (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.getValue(Identifier.fromNamespaceAndPath("client-tweaks", "blood"));
	}

	public static void init() {}
}
