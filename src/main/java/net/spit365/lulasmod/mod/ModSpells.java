package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.entity.AmethystShardEntity;
import net.spit365.lulasmod.custom.entity.MalignityEntity;
import net.spit365.lulasmod.custom.item.SpellItem;
import net.spit365.lulasmod.manager.RegisterHelper;

import java.util.LinkedList;
import java.util.List;

import static net.minecraft.sound.SoundEvents.*;

public class ModSpells {
	public static final List<Identifier> SpellTabItems = new LinkedList<>();

	public static final SpellItem SLASH_SPELL = RegisterHelper.spell("treachery_judecca", new SpellItem(3, ENTITY_ZOMBIE_VILLAGER_CURE) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			Vec3d pos = player.getRotationVec(1).normalize().multiply(2).add(player.getEyePos());
			for (Entity entity : world.getOtherEntities(player, new Box(pos.add(1d, 1d, 1d), pos.add(-1d, -1d, -1d)))) {
				if (entity instanceof LivingEntity livingEntity)
					ModMethods.applyBleed(livingEntity, (int) (120 * efficiencyMultiplier));
				else entity.discard();
			}
			world.spawnParticles(ModParticles.SCRATCH, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 0, 0);
			world.playSound(null, player.getBlockPos(), ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 100.0f, 1f);
		}
	});
	public static final SpellItem FIRE_SPELL = RegisterHelper.spell("malignity", new SpellItem(300) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			world.spawnEntity(new MalignityEntity(world, player, player.getRotationVec(1).normalize().multiply(3), Math.min(Math.round(efficiencyMultiplier + 2), 100)));
		}
	});
	public static final SpellItem DASH_SPELL = RegisterHelper.spell("purloining", new SpellItem(0) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			if (!player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.SLOWNESS)) {
				Integer usages = player.getAttached(ModData.DASH_SPELL);
				if (usages == null) usages = 5 * cooldownMultiplier;
				player.getItemCooldownManager().set(player.getStackInHand(hand), (usages.equals(1) ? (player.isOnGround() ? 20 : 40) : 5));
				player.setAttached(ModData.DASH_SPELL, (usages.equals(1) ?
						5 * cooldownMultiplier :
						Math.min(5 * cooldownMultiplier, usages) - 1)
				);
				player.addVelocity(player.getRotationVec(1).normalize().add(0, 0.25, 0));
				player.velocityModified = true;
				player.fallDistance = 0;
				world.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 25, 0.75, 0.2, 0.75, 0);
			} else player.getItemCooldownManager().set(player.getStackInHand(hand), 20);
		}
	});
	public static final SpellItem SMOKE_SPELL = RegisterHelper.spell("guile", new SpellItem(20) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			if (!player.hasStatusEffect(ModStatusEffects.CUSHIONED) && !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.INVISIBILITY)) {
				world.playSound(null, player.getBlockPos(), ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS);
				world.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, player.getPos().x, player.getPos().y + 1.0d, player.getPos().z, 269, 1.2d, 1.2d, 1.2d, 0d);
				player.addStatusEffect(new StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.INVISIBILITY, Math.round(efficiencyMultiplier - 1) * 400, 0, false, false));
				player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.CUSHIONED, Math.round(efficiencyMultiplier - 1) * 1200, 0, false, false));
			}
		}
	});
	public static final SpellItem HEAL_SPELL = RegisterHelper.spell("appeasing", new SpellItem(300) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			player.setHealth(player.getMaxHealth());
			player.getHungerManager().add(100, 0f);
		}
	});
	public static final SpellItem BLOOD_SPELL = RegisterHelper.spell("emulations", new SpellItem(0, ENTITY_ZOMBIE_VILLAGER_CURE) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			if (ModMethods.selectClosestEntity(player, 5d) instanceof LivingEntity victim)
				ModMethods.applyBleed(victim, (int) (1200 * efficiencyMultiplier) - 80);
			ModMethods.impale(player, player.getStackInHand(hand), 20, 600, 6, ModParticles.CURSED_BLOOD);
		}
	});
	public static final SpellItem HOME_SPELL = RegisterHelper.spell("wickedness", new SpellItem(600) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			ModMethods.sendHome(player, player.getStackInHand(hand).getItem());
		}
	});
	public static final SpellItem POCKET_SPELL = RegisterHelper.spell("heresies", new SpellItem(0, ENTITY_ZOMBIE_VILLAGER_CURE) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			List<Entity> entities = world.getOtherEntities(player, new Box(player.getPos().add(-5d, -5d, -5d), player.getPos().add(5d, 5d, 5d)));
			entities.removeIf(entity -> entity.getAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES) != null);
			if (entities.isEmpty()) entities.add(player);
			for (Entity victim : entities) {
				world.spawnParticles(ParticleTypes.PORTAL, victim.getX(), victim.getY() + 0.5, victim.getZ(), 50, 0, 0, 0, 1);
				if (world.getRegistryKey().equals(World.OVERWORLD) && victim instanceof ServerPlayerEntity serverPlayer) {
					ServerPlayNetworking.send(serverPlayer, new ModPackets.TimeForwardAnimationS2CPacket());
					victim.setAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES, 450);
				} else ModMethods.pocketTeleport(victim);
			}
		}
	});
	public static final SpellItem AMETHYST_SPELL = RegisterHelper.spell("envy", new SpellItem(20) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			AmethystShardEntity amethystShardEntity = new AmethystShardEntity(player, world, player.getStackInHand(hand));
			amethystShardEntity.addVelocity(player.getRotationVec(1).normalize().multiply(5));
			amethystShardEntity.setDamage(8);
			world.spawnEntity(amethystShardEntity);
		}
	});
	public static final SpellItem HIGHLIGHTER_SPELL = RegisterHelper.spell("highlighter_spell", new SpellItem(0) {
		@Override
		public void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			boolean playerGlowing = !player.isGlowing();
			world.playSound(null, player.getBlockPos(), (playerGlowing ? BLOCK_BEACON_ACTIVATE : BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
			for (PlayerEntity playerEntity : world.getPlayers()) {
				playerEntity.setGlowing(playerGlowing);
			}
		}
	});

	public static void init() {}
}
