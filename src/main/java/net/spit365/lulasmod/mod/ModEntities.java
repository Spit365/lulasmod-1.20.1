package net.spit365.lulasmod.mod;

import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityType;
import net.spit365.lulasmod.custom.entity.AmethystShardEntity;
import net.spit365.lulasmod.custom.entity.MalignityEntity;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.custom.entity.SmokeBombEntity;
import net.spit365.lulasmod.custom.entity.renderer.AmethystShardEntityRenderer;
import net.spit365.lulasmod.custom.entity.renderer.ParticleProjectileEntityRenderer;
import net.spit365.lulasmod.manager.RegisterHelper;

public class ModEntities {
	public static final EntityType<SmokeBombEntity> SMOKE_BOMB = RegisterHelper.<SmokeBombEntity>entity(
		"smoke_bomb",
		SmokeBombEntity::new,
		FlyingItemEntityRenderer::new,
		0.25F, 0.25F, 4, 10);
	public static final EntityType<MalignityEntity> MALIGNITY = RegisterHelper.<MalignityEntity>entity(
		"malignity",
		MalignityEntity::new,
		FlyingItemEntityRenderer::new,
		1.0F, 1.0F, 4, 10);
	public static final EntityType<ParticleProjectileEntity> PARTICLE_PROJECTILE = RegisterHelper.<ParticleProjectileEntity>entity(
		"particle_projectile",
		ParticleProjectileEntity::new,
		ParticleProjectileEntityRenderer::new,
		0.5F, 0.5F, 4, 20);
	public static final EntityType<AmethystShardEntity> AMETHYST_SHARD = RegisterHelper.<AmethystShardEntity>entity(
		"amethyst_shard",
		AmethystShardEntity::new,
		AmethystShardEntityRenderer::new,
		0.5f, 0.5f, 4, 20);

	public static void init() {}
}
