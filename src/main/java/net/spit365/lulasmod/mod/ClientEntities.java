package net.spit365.lulasmod.mod;

import net.spit365.lulasmod.renderer.AmethystShardEntityRenderer;
import net.spit365.lulasmod.renderer.NeedleSwordEntityRenderer;
import net.spit365.lulasmod.util.ClientRegisterHelper;

import static net.spit365.lulasmod.mod.ModEntities.*;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public final class ClientEntities {

	public static void init() {
		ClientRegisterHelper.entity(SMOKE_BOMB, ThrownItemRenderer::new);
		ClientRegisterHelper.entity(SMOKE_PROJECTILE, NoopRenderer::new);
		ClientRegisterHelper.entity(MALIGNITY, ThrownItemRenderer::new);
		ClientRegisterHelper.entity(PARTICLE_PROJECTILE, NoopRenderer::new);
		ClientRegisterHelper.entity(AMETHYST_SHARD, AmethystShardEntityRenderer::new);
		ClientRegisterHelper.entity(NEEDLE_SWORD, NeedleSwordEntityRenderer::new);
	}
}
