package net.spit365.lulasmod.mod;

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
import net.spit365.lulasmod.custom.Impaled;
import net.spit365.lulasmod.entity.AmethystShardEntity;
import net.spit365.lulasmod.entity.MalignityEntity;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import net.spit365.lulasmod.item.spell.SorceryItem;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.state.LinkedLightningPersistentState;
import net.spit365.lulasmod.util.MultiVec3d;
import net.spit365.lulasmod.util.RegisterHelper;

import java.util.*;
import java.util.stream.StreamSupport;

import static net.minecraft.sound.SoundEvents.*;
import static net.spit365.lulasmod.state.LinkedLightningPersistentState.lastLinks;

public class ModSpells {
	public static final List<Identifier> SpellTabItems = new LinkedList<>();

    public static final SpellItem HIGHLIGHTER_SPELL = RegisterHelper.spell("highlighter_spell",  settings -> new SpellItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
        boolean playerGlowing = !player.isGlowing();
        world.playSound(null, player.getBlockPos(), (playerGlowing ? BLOCK_BEACON_ACTIVATE : BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
        for (PlayerEntity playerEntity : world.getPlayers()) playerEntity.setGlowing(playerGlowing);
        return 0;
    }));

	public static final SpellItem FIRE_SPELL = RegisterHelper.spell("malignity",  settings -> new SpellItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
		world.spawnEntity(new MalignityEntity(world, player, player.getRotationVec(1).normalize().multiply(3), Math.min(Math.round(efficiencyMultiplier + 2), 100)));
		return 300;
	}));
	public static final SpellItem DASH_SPELL = RegisterHelper.spell("purloining",  settings -> new SpellItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
		if (!player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.SLOWNESS)) {
			Integer usages = player.getAttached(ModData.DASH_SPELL);
			if (usages == null) usages = 5 * cooldownMultiplier;
			player.setAttached(ModData.DASH_SPELL, (usages.equals(1) ?
				5 * cooldownMultiplier :
				Math.min(5 * cooldownMultiplier, usages) - 1)
			);
			boolean onGround = player.isOnGround();
			player.addVelocity(player.getRotationVec(1).normalize().add(0, 0.25, 0));
			player.velocityModified = true;
			player.fallDistance = 0;
			world.spawnParticles(ParticleTypes.CLOUD, player.getX(), player.getY(), player.getZ(), 25, 0.75, 0.2, 0.75, 0);
			return usages.equals(1) ? (onGround ? 20 : 40) : 5;
		}
		return 20;
	}));
	public static final SpellItem SMOKE_SPELL = RegisterHelper.spell("guile",  settings -> new SpellItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
		if (!player.hasStatusEffect(ModStatusEffects.CUSHIONED) && !player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.INVISIBILITY)) {
			world.playSound(null, player.getBlockPos(), ENTITY_SPLASH_POTION_BREAK, SoundCategory.PLAYERS);
			world.spawnParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE, player.getPos().x, player.getPos().y + 1.0d, player.getPos().z, 269, 1.2d, 1.2d, 1.2d, 0d);
			player.addStatusEffect(new StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.INVISIBILITY, Math.round(efficiencyMultiplier - 1) * 400, 0, false, false));
			player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.CUSHIONED, Math.round(efficiencyMultiplier - 1) * 1200, 0, false, false));
		}
		return 20;
	}));
	public static final SpellItem HEAL_SPELL = RegisterHelper.spell("appeasing",  settings -> new SpellItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
		player.setHealth(player.getMaxHealth());
		player.getHungerManager().add(100, 0f);
		return 300;
	}));


	public static final SpellItem HOME_SPELL = RegisterHelper.spell("wickedness", settings -> new SpellItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
		ModMethods.sendHome(player, player.getStackInHand(hand).getItem());
		return 600;
	}));
	public static final SpellItem AMETHYST_SPELL = RegisterHelper.spell("envy", settings -> new SpellItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
		AmethystShardEntity amethystShardEntity = new AmethystShardEntity(player, world);
		amethystShardEntity.addVelocity(player.getRotationVec(1).normalize().multiply(5));
		amethystShardEntity.setDamage(8);
		world.spawnEntity(amethystShardEntity);
		return 20;
	}));

    public static final ConjuringItem SLASH_CONJURING = RegisterHelper.spell("treachery_judecca",  settings -> new ConjuringItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
        Vec3d pos = player.getRotationVec(1).normalize().multiply(2).add(player.getEyePos());
        for (Entity entity : world.getOtherEntities(player, new Box(pos.add(1d, 1d, 1d), pos.add(-1d, -1d, -1d)))) {
            if (entity instanceof LivingEntity livingEntity)
                ModMethods.applyBleed(livingEntity, (int) (120 * efficiencyMultiplier));
            else entity.discard();
        }
        world.spawnParticles(ModParticles.SCRATCH, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 0, 0);
        world.playSound(null, player.getBlockPos(), ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 100.0f, 1f);
        return 3;
    }));
    public static final ConjuringItem BLOOD_CONJURING = RegisterHelper.spell("emulations", settings -> new ConjuringItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
        if (ModMethods.selectClosestEntity(player, 5d) instanceof LivingEntity victim)
            ModMethods.applyBleed(victim, (int) (1200 * efficiencyMultiplier) - 80);
        Impaled.impale(player, player.getStackInHand(hand), 20, 600, 6, 25, ModParticles.CURSED_BLOOD);
        return 0;
    }));
    public static final ConjuringItem POCKET_CONJURING = RegisterHelper.spell("heresies", settings -> new ConjuringItem(settings, (world, player, hand, efficiencyMultiplier, cooldownMultiplier) -> {
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
        return 0;
    }));

	public static final SorceryItem COMBUSTION_SORCERY = RegisterHelper.spell("combustion", settings -> new SorceryItem(settings, new SorceryItem.Sorcery() {
		@Override
		public int hitEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			if (!player.getItemCooldownManager().isCoolingDown(player.getStackInHand(hand))) {
				player.getWorld().createExplosion(player, target.getX(), target.getY(), target.getZ(), player.distanceTo(target) / 4, World.ExplosionSourceType.NONE);
				player.damage(world, world.getDamageSources().inFire(), 2);
				return 100;
			}
			return -1;
		}

		@Override public int useEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, Float efficiencyMultiplier, Integer cooldownMultiplier) {return -1;}

		@Override
		public int cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
            Vec3d eyePos = player.getEyePos();
            Vec3d[] laser = new MultiVec3d(eyePos, eyePos.add(player.getRotationVec(1).normalize().multiply(25))).stream().toArray(Vec3d[]::new);
            for (int i = 1; i < laser.length; i++) {
                Vec3d currentPos = laser[i];
                world.spawnParticles(ParticleTypes.FLAME, currentPos.getX(), currentPos.getY(), currentPos.getZ(), 60 / MultiVec3d.MULTI_VEC_DETAIL, 0.25, 0.25, 0.25, 0);
                world.getOtherEntities(player, new Box(laser[i -1], currentPos)).forEach(entity -> {
                    entity.damage(world, world.getDamageSources().inFire(), 4);
                    entity.setOnFireFor(entity.getFireTicks() / 20f + 3);
                });
            }
            return -1;
		}

		@Override public int castTick(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, Float efficiencyMultiplier, Integer cooldownMultiplier) {return -1;}
		@Override public int castStop(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, Float efficiencyMultiplier, Integer cooldownMultiplier) {return -1;}
	}));
    public static final SorceryItem CURRENT_SORCERY = RegisterHelper.spell("current", settings -> new SorceryItem(settings, new SorceryItem.Sorcery() {
		@Override public int hitEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, Float efficiencyMultiplier, Integer cooldownMultiplier) {return -1;}
		@Override public int useEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, Float efficiencyMultiplier, Integer cooldownMultiplier) {return -1;}

        @Override
        public int cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
            player.setCurrentHand(hand);
			return 0;
        }

        @Override
        public int castTick(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			MultiVec3d lastLink = lastLinks.get(player);
			int maxDistance = 100;
			Vec3d raycast = player.raycast(maxDistance, 1, false).getPos();
			Vec3d eyePos = player.getEyePos();
			boolean failedResult = raycast.distanceTo(eyePos) >= maxDistance;
			if (lastLink == null) {
				if (failedResult) return -1;
				lastLinks.put(player, new MultiVec3d(raycast, raycast));
			}else{
                MultiVec3d value = new MultiVec3d(lastLink.get(0), !failedResult? raycast : eyePos.add(player.getRotationVec(1).normalize().multiply(eyePos.distanceTo(lastLink.get(0)))));
				lastLinks.put(player, value);
				LinkedLightningPersistentState linkedLightnings = LinkedLightningPersistentState.get(world);
				linkedLightnings.remove(lastLink);
				linkedLightnings.add(value);
			}
			return 0;
		}

        @Override
        public int castStop(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, Float efficiencyMultiplier, Integer cooldownMultiplier) {
			lastLinks.remove(player);
			return 100;
        }
    }));
    public static final SorceryItem KINESIS_SORCERY = RegisterHelper.spell("kinesis", settings -> new SorceryItem(settings, new SorceryItem.Sorcery() {
        private static final HashMap<Entity, List<Entity>> selectedEntities = new HashMap<>();

        @Override
        public int hitEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, Float efficiencyMultiplier, Integer cooldownMultiplier) {
            player.addVelocity(player.getPos().subtract(target.getPos()).normalize().multiply(5));
            player.fallDistance = 0;
            player.velocityModified = true;
            return 0;
        }

        @Override public int useEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, Float efficiencyMultiplier, Integer cooldownMultiplier) {return -1;}

        @Override
        public int cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier) {
            player.setCurrentHand(hand);
            if (selectedEntities.get(player) == null){
                selectedEntities.put(player, selectEntities(world, player.getEyePos(), player.getRotationVec(1).normalize()));
                return 0;
            }
            return -1;
        }

        @Override public int castTick(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, Float efficiencyMultiplier, Integer cooldownMultiplier) {return -1;}

        @Override
        public int castStop(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, Float efficiencyMultiplier, Integer cooldownMultiplier) {
            List<Entity> selectedEntities = this.selectedEntities.remove(player);
            if (selectedEntities != null && !selectedEntities.isEmpty()) {
                for (Entity selectedEntity : selectedEntities) {
                    Vec3d relativeEntityCoordinates = selectedEntity.getPos().subtract(player.getPos());
                    selectedEntity.addVelocity(player.getRotationVec(1).normalize().multiply(relativeEntityCoordinates.length()).subtract(relativeEntityCoordinates).multiply(0.25));
                    selectedEntity.velocityModified = true;
                }
                return 0;
            }
            return -1;
        }

        private static List<Entity> selectEntities(ServerWorld world, Vec3d center, Vec3d rotation){
            return StreamSupport.stream(world.iterateEntities().spliterator(), false).filter(entity ->
                    rotation.dotProduct(entity.getPos().subtract(center).normalize()) >= Math.cos(Math.toRadians(20))
            ).toList();
        }
    }));


	public static void init() {}
}
