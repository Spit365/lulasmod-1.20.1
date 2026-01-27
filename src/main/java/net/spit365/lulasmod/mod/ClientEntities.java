package net.spit365.lulasmod.mod;

import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.spit365.lulasmod.renderer.AmethystShardEntityRenderer;
import net.spit365.lulasmod.renderer.EmptyRenderer;
import net.spit365.lulasmod.renderer.NeedleSwordEntityRenderer;
import net.spit365.lulasmod.util.ClientRegisterHelper;

import static net.spit365.lulasmod.mod.ModEntities.*;

public final class ClientEntities {

	public static void init() {
		ClientRegisterHelper.entity(SMOKE_BOMB, FlyingItemEntityRenderer::new);
		ClientRegisterHelper.entity(SMOKE_PROJECTILE, EmptyRenderer::new);
		ClientRegisterHelper.entity(MALIGNITY, FlyingItemEntityRenderer::new);
		ClientRegisterHelper.entity(PARTICLE_PROJECTILE, EmptyRenderer::new);
		ClientRegisterHelper.entity(AMETHYST_SHARD, AmethystShardEntityRenderer::new);
		ClientRegisterHelper.entity(NEEDLE_SWORD, NeedleSwordEntityRenderer::new);
	}
}
