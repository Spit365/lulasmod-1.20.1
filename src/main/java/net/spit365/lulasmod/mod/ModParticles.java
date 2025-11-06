package net.spit365.lulasmod.mod;

import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.particle.SimpleParticleType;
import net.spit365.lulasmod.util.RegisterHelper;

public class ModParticles {
	public static final SimpleParticleType SCRATCH = RegisterHelper.particle("scratch", true, SweepAttackParticle.Factory::new);
	public static final SimpleParticleType GOLDEN_SHIMMER = RegisterHelper.particle("golden_shimmer", false, FlameParticle.Factory::new);
	public static final SimpleParticleType CURSED_BLOOD = RegisterHelper.particle("cursed_blood", false, FlameParticle.Factory::new);
	public static final SimpleParticleType EXPLOSION = RegisterHelper.particle("explosion", false, ExplosionLargeParticle.Factory::new);

	public static void init() {}
}
