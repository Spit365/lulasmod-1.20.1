package net.spit365.lulasmod.mod;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;
import net.spit365.lulasmod.blockentity.TitrationStandBlockEntity;
import net.spit365.lulasmod.entity.*;
import net.spit365.lulasmod.util.RegisterHelper;

public final class ModEntities {
	public static final EntityType<SmokeBombEntity> SMOKE_BOMB = RegisterHelper.entity(
		"smoke_bomb",
		SmokeBombEntity::new,
		0.25F, 0.25F, 4, 10);
    public static final EntityType<SmokeProjectileEntity> SMOKE_PROJECTILE = RegisterHelper.entity(
		"smoke_projectile",
		SmokeProjectileEntity::new,
		0.25F, 0.25F, 4, 10);
	public static final EntityType<MalignityEntity> MALIGNITY = RegisterHelper.entity(
		"malignity",
		MalignityEntity::new,
		1.0F, 1.0F, 4, 10);
	public static final EntityType<ParticleProjectileEntity> PARTICLE_PROJECTILE = RegisterHelper.entity(
		"particle_projectile",
		ParticleProjectileEntity::new,
		0.5F, 0.5F, 4, 20);
	public static final EntityType<AmethystShardEntity> AMETHYST_SHARD = RegisterHelper.entity(
		"amethyst_shard",
		AmethystShardEntity::new,
		0.5f, 0.5f, 4, 20);
    public static final EntityType<NeedleSwordEntity> NEEDLE_SWORD = RegisterHelper.entity(
		"needle_sword",
        NeedleSwordEntity::new,
		0.5f, 0.5f, 4, 20);
	public static final BlockEntityType<TitrationStandBlockEntity> TITRATION_STAND = RegisterHelper.blockEntity(
		"titration_stand", TitrationStandBlockEntity::new);

	public static void init() {}
}
