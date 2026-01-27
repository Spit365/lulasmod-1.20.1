package net.spit365.lulasmod.mod;

import net.minecraft.client.particle.ExplosionLargeParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.SweepAttackParticle;
import net.spit365.lulasmod.util.ClientRegisterHelper;

import static net.spit365.lulasmod.mod.ModParticles.*;

public final class ClientParticles {
	public static void init() {
		ClientRegisterHelper.particle(SCRATCH, SweepAttackParticle.Factory::new);
		ClientRegisterHelper.particle(GOLDEN_SHIMMER, FlameParticle.Factory::new);
		ClientRegisterHelper.particle(CURSED_BLOOD, FlameParticle.Factory::new);
		ClientRegisterHelper.particle(EXPLOSION, ExplosionLargeParticle.Factory::new);
	}
}
