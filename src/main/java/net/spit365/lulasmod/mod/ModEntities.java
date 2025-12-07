package net.spit365.lulasmod.mod;

import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.EntityType;
import net.spit365.lulasmod.entity.*;
import net.spit365.lulasmod.renderer.*;
import net.spit365.lulasmod.util.RegisterHelper;

public class ModEntities {
	public static final EntityType<SmokeBombEntity> SMOKE_BOMB = RegisterHelper.entity(
		"smoke_bomb",
		SmokeBombEntity::new,
		FlyingItemEntityRenderer::new,
		0.25F, 0.25F, 4, 10);
    public static final EntityType<SmokeProjectileEntity> SMOKE_PROJECTILE = RegisterHelper.entity(
		"smoke_projectile",
		SmokeProjectileEntity::new,
        EmptyRenderer::new,
		0.25F, 0.25F, 4, 10);
	public static final EntityType<MalignityEntity> MALIGNITY = RegisterHelper.entity(
		"malignity",
		MalignityEntity::new,
		FlyingItemEntityRenderer::new,
		1.0F, 1.0F, 4, 10);
	public static final EntityType<ParticleProjectileEntity> PARTICLE_PROJECTILE = RegisterHelper.entity(
		"particle_projectile",
		ParticleProjectileEntity::new,
		EmptyRenderer::new,
		0.5F, 0.5F, 4, 20);
	public static final EntityType<AmethystShardEntity> AMETHYST_SHARD = RegisterHelper.entity(
		"amethyst_shard",
		AmethystShardEntity::new,
		AmethystShardEntityRenderer::new,
		0.5f, 0.5f, 4, 20);
    public static final EntityType<NeedleSwordEntity> NEEDLE_SWORD = RegisterHelper.entity(
		"needle_sword",
        NeedleSwordEntity::new,
		NeedleSwordEntityRenderer::new,
		0.5f, 0.5f, 4, 20);

	public static void init() {}
}
