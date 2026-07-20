package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.spit365.boa.BoxOutline;
import net.spit365.lulasmod.custom.*;
import net.spit365.lulasmod.entity.AmethystShardEntity;
import net.spit365.lulasmod.entity.MalignityEntity;
import net.spit365.lulasmod.entity.SmokeProjectileEntity;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import net.spit365.lulasmod.item.spell.SorceryItem;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.packet.SetTimeForwardAnimationStateS2CPacket;
import net.spit365.lulasmod.renderer.KinesisInteractionRenderer;
import net.spit365.lulasmod.state.LinkedLightningPersistentState;
import net.spit365.lulasmod.util.ModUtil;
import net.spit365.lulasmod.util.MultiVec3d;
import net.spit365.lulasmod.util.RegisterHelper;
import net.spit365.lulasmod.util.Spell;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import static net.minecraft.sounds.SoundEvents.*;
import static net.spit365.lulasmod.item.SealItem.FAIL_RESULT;
import static net.spit365.lulasmod.item.SealItem.NO_COOLDOWN_RESULT;
import static net.spit365.lulasmod.state.LinkedLightningPersistentState.lastLinks;

public final class ModSpells {
	public static final List<ItemStack> SpellTabItems = new LinkedList<>();

    public static final SpellItem HIGHLIGHTER_SPELL = RegisterHelper.spell("highlighter_spell",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        boolean playerGlowing = !player.isCurrentlyGlowing();
        world.playSound(null, player.blockPosition(), (playerGlowing ? BEACON_ACTIVATE : BEACON_DEACTIVATE), SoundSource.PLAYERS);
        for (Player playerEntity : world.players()) playerEntity.setGlowingTag(playerGlowing);
        return NO_COOLDOWN_RESULT;
    }));

	public static final SpellItem FIRE_SPELL = RegisterHelper.spell("malignity",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
		world.addFreshEntity(new MalignityEntity(world, player, player.getViewVector(1).normalize().scale(0.5), Math.min(Math.round(potencyMultiplier + 2), 100)));
		return 300;
	}));
	public static final SpellItem DASH_SPELL = RegisterHelper.spell("purloining",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> DashSpell.onUse(world, player, cooldownDivisor)));
	public static final SpellItem SMOKE_SPELL = RegisterHelper.spell("guile",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
		if (!SmokeSpellCooldown.isCoolingDown(player)) {
            world.addFreshEntity(new SmokeProjectileEntity(world, player, ItemStack.EMPTY));
            if (potencyMultiplier > 1) {
                int duration = (int) ((potencyMultiplier - 1) * 200);
                for (Holder<MobEffect> effect : Set.of(net.minecraft.world.effect.MobEffects.INVISIBILITY, ModStatusEffects.CUSHIONED))
                    player.addEffect(new MobEffectInstance(effect, duration, 0, false, false));
            }
            SmokeSpellCooldown.apply(player, cooldownDivisor);
            return 20;
		} else {
            player.displayClientMessage(Component.translatable("notify.lulasmod.smoke_cooling_down").append(Component.literal(": " + SmokeSpellCooldown.getPercent(player) + "%")), true);
            return FAIL_RESULT;
        }
	}));
	public static final SpellItem HEAL_SPELL = RegisterHelper.spell("appeasing",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        FoodData hungerManager = player.getFoodData();
		player.heal(hungerManager.getFoodLevel() - 1);
        hungerManager.setFoodLevel(1);
		hungerManager.setSaturation(0);
		return 300;
	}));


	public static final SpellItem HOME_SPELL = RegisterHelper.spell("wickedness", settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
		ModUtil.sendHome(player, player.getItemInHand(hand).getItem());
		return 600;
	}));
	public static final SpellItem AMETHYST_SPELL = RegisterHelper.spell("envy", settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        world.addFreshEntity(new AmethystShardEntity(player, world));
		return 20;
	}));

    public static final ConjuringItem SLASH_CONJURING = RegisterHelper.spell("treachery_judecca",  settings -> new ConjuringItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        Vec3 pos = player.getViewVector(1).normalize().scale(2).add(player.getEyePosition());
        for (Entity entity : world.getEntities(player, new AABB(pos.add(1), pos.add(-1)))) {
            if (entity instanceof LivingEntity livingEntity)
                Bleed.apply(livingEntity, (int) (120 * potencyMultiplier));
            else entity.discard();
        }
        world.sendParticles(ModParticles.SCRATCH, pos.x(), pos.y(), pos.z(), 0, 0, 0, 0, 0);
        world.playSound(null, player.blockPosition(), PLAYER_ATTACK_STRONG, SoundSource.PLAYERS, 1, 1);
        return 3;
    }));
    public static final ConjuringItem BLOOD_CONJURING = RegisterHelper.spell("emulations", settings -> new ConjuringItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        Entity target = ModUtil.selectClosestEntity(player, 5);
        if (target instanceof LivingEntity livingEntity)
            Bleed.apply(livingEntity, (int) (1200 * potencyMultiplier) - 80);
        ItemStack item = player.getItemInHand(hand);
        if (Impaled.impale(player, target, item, 6, 25, ModParticles.CURSED_BLOOD))
            player.getCooldowns().addCooldown(item, 600);
        return 20;
    }));
    public static final ConjuringItem POCKET_CONJURING = RegisterHelper.spell("heresies", settings -> new ConjuringItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        AABB box = new AABB(player.position().add(5), player.position().add(-5));
        List<Entity> entities = world.getEntities(player, box);
        entities.removeIf(entity -> entity.getAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES) != null);
        if (entities.isEmpty()) entities.add(player);
        for (Entity victim : entities) {
            BoxOutline.add(victim.getBoundingBox(), 0xFFFF0000);
            if (world.dimension().equals(Level.OVERWORLD) && victim instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, new SetTimeForwardAnimationStateS2CPacket(true));
                victim.setAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES, new TimeForward.VisualContext(TimeForward.ANIMATION_DURATION, player.position()));
            } else ModUtil.pocketTeleport(victim);
        }
        return NO_COOLDOWN_RESULT;
    }));

	public static final SorceryItem COMBUSTION_SORCERY = RegisterHelper.spell("combustion", settings -> new SorceryItem(settings, new Spell() {
		@Override
		public int hitEntity(ServerLevel world, Player player, InteractionHand hand, LivingEntity target, float potencyMultiplier, int cooldownDivisor) {
			if (!player.getCooldowns().isOnCooldown(player.getItemInHand(hand))) {
				player.level().explode(player, target.getX(), target.getY(), target.getZ(), player.distanceTo(target) / 4, Level.ExplosionInteraction.NONE);
				player.hurtServer(world, world.damageSources().inFire(), 2);
				return 100;
			}
			return FAIL_RESULT;
		}

		@Override
		public int cast(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownDivisor) {
            Vec3 eyePos = player.getEyePosition();
            Vec3[] laser = new MultiVec3d(eyePos, eyePos.add(player.getViewVector(1).normalize().scale(25))).stream().toArray(Vec3[]::new);
            for (int i = 1; i < laser.length; i++) {
                Vec3 currentPos = laser[i];
                world.sendParticles(ParticleTypes.FLAME, currentPos.x(), currentPos.y(), currentPos.z(), 60 / MultiVec3d.MULTI_VEC_DETAIL, 0.25, 0.25, 0.25, 0);
                world.getEntities(player, new AABB(laser[i -1], currentPos)).forEach(entity -> {
                    entity.hurtServer(world, world.damageSources().inFire(), 4);
                    entity.igniteForSeconds(entity.getRemainingFireTicks() / 20f + 3);
                });
            }
            return FAIL_RESULT;
		}

	}));
    public static final SorceryItem CURRENT_SORCERY = RegisterHelper.spell("current", settings -> new SorceryItem(settings, new Spell() {
		@Override
        public int cast(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownDivisor) {
            player.startUsingItem(hand);
			return NO_COOLDOWN_RESULT;
        }

        @Override
        public int castTick(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownDivisor) {
			MultiVec3d lastLink = lastLinks.get(player);
			int maxDistance = 100;
			Vec3 raycast = player.pick(maxDistance, 1, false).getLocation();
			Vec3 eyePos = player.getEyePosition();
			boolean failedResult = raycast.distanceTo(eyePos) >= maxDistance;
			if (lastLink == null) {
				if (failedResult) return FAIL_RESULT;
				lastLinks.put(player, new MultiVec3d(raycast, raycast));
			}else{
                MultiVec3d value = new MultiVec3d(lastLink.get(0), !failedResult? raycast : eyePos.add(player.getViewVector(1).normalize().scale(eyePos.distanceTo(lastLink.get(0)))));
				lastLinks.put(player, value);
				LinkedLightningPersistentState linkedLightnings = LinkedLightningPersistentState.get(world);
				linkedLightnings.remove(lastLink);
				linkedLightnings.add(value);
			}
			return NO_COOLDOWN_RESULT;
		}

        @Override
        public int castStop(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownDivisor) {
			lastLinks.remove(player);
			return 100;
        }
    }));
	public static final SorceryItem KINESIS_SORCERY = RegisterHelper.spell("kinesis", settings -> new SorceryItem(settings, new Spell() {
        private static final HashMap<Entity, List<Entity>> selectedEntities = new HashMap<>();

        @Override
        public int hitEntity(ServerLevel world, Player player, InteractionHand hand, LivingEntity target, float potencyMultiplier, int cooldownDivisor) {
            Vec3 vec3d = player.position().subtract(target.position()).normalize().scale(2);
            if (player.onGround()) target.push(vec3d.scale(-1).add(0, 0.25, 0));
            else player.push(vec3d);
            player.fallDistance = 0;
            player.hurtMarked = true;
            return NO_COOLDOWN_RESULT;
        }

		@Override
        public int cast(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownDivisor) {
            player.startUsingItem(hand);
            if (selectedEntities.get(player) == null) {
                selectedEntities.put(player, StreamSupport.stream(world.getAllEntities().spliterator(), false)
                    .filter(entity -> player.getViewVector(1).normalize().dot(entity.getEyePosition().subtract(player.getEyePosition()).normalize()) >= Math.cos(KinesisInteractionRenderer.INTERACTION_RANGE_RADIANS)).toList()
                );
                return NO_COOLDOWN_RESULT;
            }
            return FAIL_RESULT;
        }

		@Override
        public int castStop(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownDivisor) {
            List<Entity> selectedEntities = this.selectedEntities.remove(player);
            if (selectedEntities != null && !selectedEntities.isEmpty()) {
                for (Entity selectedEntity : selectedEntities) {
					selectedEntity.setDeltaMovement(player.getViewVector(1).normalize().subtract(selectedEntity.position().subtract(player.position()).normalize()).normalize().scale(2));
                    selectedEntity.hurtMarked = true;
                }
                return NO_COOLDOWN_RESULT;
            }
            return FAIL_RESULT;
        }

		@Override
		public int castTick(ServerLevel world, Player player, InteractionHand hand, float potencyMultiplier, int cooldownDivisor) {
			if (world instanceof ServerLevel serverWorld) player.hurtServer(serverWorld, ModDamageTypes.createDamageSource(world, ModDamageTypes.KINETIC_BACKLASH), 1);
			return NO_COOLDOWN_RESULT;
		}
    }));


	public static void init() {}
}
