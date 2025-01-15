package net.spit365.lulasmod;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lulasmod implements ModInitializer {
	public static final String MOD_ID = "lulasmod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Registry.register(Registries.PARTICLE_TYPE, new Identifier(Lulasmod.MOD_ID, "scratch"), SCRATCH);
		ParticleFactoryRegistry.getInstance().register(Lulasmod.SCRATCH, SweepAttackParticle.Factory::new);
		UseItemCallback.EVENT.register((player, world, hand) -> {
		if (!world.isClient) {
			if (player.getStackInHand(hand).getItem() == ModItems.MODIFIED_TNT) {
				player.setVelocity(0, 1, 0);
				BlockPos pos1 = BlockPos.ofFloored(player.raycast(999999, 1, false).getPos());
				world.createExplosion(player, pos1.getX(), pos1.getY(), pos1.getZ(), 5, World.ExplosionSourceType.TNT);
				if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
			}
			if (player.getStackInHand(hand).getItem() == ModItems.DRAGON_FIREBALL) {
				DragonFireballEntity fireballEntity = new DragonFireballEntity(world, player, player.getRotationVec(1f).getX(), player.getRotationVec(1f).getY(), player.getRotationVec(1f).getZ());
				fireballEntity.setPosition(player.getX(), player.getY() + 1, player.getZ());
				world.spawnEntity(fireballEntity);
				if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
			}
			if (player.getStackInHand(hand).getItem() == ModItems.HIGHLIGHTER) {
				boolean iPlayerGlowing = !player.isGlowing();
				for (int i = 0; i <= world.getPlayers().size(); i++){world.getPlayers().get(i).setGlowing(iPlayerGlowing);}
				if (!player.isCreative()) {player.getStackInHand(hand).decrement(1);}
			}
			if (player.getStackInHand(hand).getItem() == ModItems.LIGHTNING_CRYSTAL) {
				BlockPos pos2 = BlockPos.ofFloored(player.raycast(100, 1, false).getPos());
				Entity LightningEntity = new Entity(EntityType.LIGHTNING_BOLT, world) {
					@Override
					protected void initDataTracker() {

					}

					@Override
					protected void readCustomDataFromNbt(NbtCompound nbt) {

					}

					@Override
					protected void writeCustomDataToNbt(NbtCompound nbt) {

					}
				};
				world.spawnEntity(LightningEntity);
				LightningEntity.setPosition(new Vec3d(pos2.getX(), pos2.getY(), pos2.getZ()));
				world.createExplosion(LightningEntity,pos2.getX(), pos2.getY(), pos2.getZ(), 5, World.ExplosionSourceType.NONE);
				player.damage(new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.MAGIC)), 4f);
			}
			if (player.getStackInHand(hand).getItem() == ModItems.GRAVITATOR) {
				player.setNoGravity(!player.hasNoGravity());
			}

		}
		return TypedActionResult.pass(player.getStackInHand(hand));
	});
		ModItems.registerModItems();
		LOGGER.info("Hello Fabric world!");
	}
	public static final DefaultParticleType SCRATCH = FabricParticleTypes.simple(true);

}