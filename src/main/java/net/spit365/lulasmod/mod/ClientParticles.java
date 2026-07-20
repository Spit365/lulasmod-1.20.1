package net.spit365.lulasmod.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AttackSweepParticle;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.particle.HugeExplosionParticle;
import net.spit365.lulasmod.util.ClientRegisterHelper;

import static net.spit365.lulasmod.mod.ModParticles.*;

@Environment(EnvType.CLIENT)
public final class ClientParticles {
	public static void init() {
		ClientRegisterHelper.particle(SCRATCH, AttackSweepParticle.Provider::new);
		ClientRegisterHelper.particle(GOLDEN_SHIMMER, FlameParticle.Provider::new);
		ClientRegisterHelper.particle(CURSED_BLOOD, FlameParticle.Provider::new);
		ClientRegisterHelper.particle(EXPLOSION, HugeExplosionParticle.Provider::new);
	}
}
