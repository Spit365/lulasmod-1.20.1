package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
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

import static net.minecraft.sound.SoundEvents.*;
import static net.spit365.lulasmod.item.SealItem.FAIL_RESULT;
import static net.spit365.lulasmod.item.SealItem.NO_COOLDOWN_RESULT;
import static net.spit365.lulasmod.state.LinkedLightningPersistentState.lastLinks;

public final class ModSpells {
	public static final List<ItemStack> SpellTabItems = new LinkedList<>();

    public static final SpellItem HIGHLIGHTER_SPELL = RegisterHelper.spell("highlighter_spell",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        boolean playerGlowing = !player.isGlowing();
        world.playSound(null, player.getBlockPos(), (playerGlowing ? BLOCK_BEACON_ACTIVATE : BLOCK_BEACON_DEACTIVATE), SoundCategory.PLAYERS);
        for (PlayerEntity playerEntity : world.getPlayers()) playerEntity.setGlowing(playerGlowing);
        return NO_COOLDOWN_RESULT;
    }));

	public static final SpellItem FIRE_SPELL = RegisterHelper.spell("malignity",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
		world.spawnEntity(new MalignityEntity(world, player, player.getRotationVec(1).normalize().multiply(3), Math.min(Math.round(potencyMultiplier + 2), 100)));
		return 300;
	}));
	public static final SpellItem DASH_SPELL = RegisterHelper.spell("purloining",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> DashSpell.onUse(world, player, cooldownDivisor)));
	public static final SpellItem SMOKE_SPELL = RegisterHelper.spell("guile",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
		if (!SmokeSpellCooldown.isCoolingDown(player)) {
            world.spawnEntity(new SmokeProjectileEntity(world, player, ItemStack.EMPTY));
            if (potencyMultiplier > 1) {
                int duration = (int) ((potencyMultiplier - 1) * 200);
                for (RegistryEntry<StatusEffect> effect : Set.of(net.minecraft.entity.effect.StatusEffects.INVISIBILITY, ModStatusEffects.CUSHIONED))
                    player.addStatusEffect(new StatusEffectInstance(effect, duration, 0, false, false));
            }
            SmokeSpellCooldown.apply(player, cooldownDivisor);
            return 20;
		} else {
            player.sendMessage(Text.translatable("notify.lulasmod.smoke_cooling_down").append(Text.literal(": " + SmokeSpellCooldown.getPercent(player) + "%")), true);
            return FAIL_RESULT;
        }
	}));
	public static final SpellItem HEAL_SPELL = RegisterHelper.spell("appeasing",  settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        HungerManager hungerManager = player.getHungerManager();
		player.heal(hungerManager.getFoodLevel() - 1);
        hungerManager.setFoodLevel(1);
		hungerManager.setSaturationLevel(0);
		return 300;
	}));


	public static final SpellItem HOME_SPELL = RegisterHelper.spell("wickedness", settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
		ModUtil.sendHome(player, player.getStackInHand(hand).getItem());
		return 600;
	}));
	public static final SpellItem AMETHYST_SPELL = RegisterHelper.spell("envy", settings -> new SpellItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        world.spawnEntity(new AmethystShardEntity(player, world));
		return 20;
	}));

    public static final ConjuringItem SLASH_CONJURING = RegisterHelper.spell("treachery_judecca",  settings -> new ConjuringItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        Vec3d pos = player.getRotationVec(1).normalize().multiply(2).add(player.getEyePos());
        for (Entity entity : world.getOtherEntities(player, new Box(pos.add(1), pos.add(-1)))) {
            if (entity instanceof LivingEntity livingEntity)
                Bleed.apply(livingEntity, (int) (120 * potencyMultiplier));
            else entity.discard();
        }
        world.spawnParticles(ModParticles.SCRATCH, pos.getX(), pos.getY(), pos.getZ(), 0, 0, 0, 0, 0);
        world.playSound(null, player.getBlockPos(), ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1, 1);
        return 3;
    }));
    public static final ConjuringItem BLOOD_CONJURING = RegisterHelper.spell("emulations", settings -> new ConjuringItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        Entity target = ModUtil.selectClosestEntity(player, 5);
        if (target instanceof LivingEntity livingEntity)
            Bleed.apply(livingEntity, (int) (1200 * potencyMultiplier) - 80);
        Impaled.impale(player, target, player.getStackInHand(hand), 20, 600, 6, 25, ModParticles.CURSED_BLOOD);
        return NO_COOLDOWN_RESULT;
    }));
    public static final ConjuringItem POCKET_CONJURING = RegisterHelper.spell("heresies", settings -> new ConjuringItem(settings, (world, player, hand, potencyMultiplier, cooldownDivisor) -> {
        Box box = new Box(player.getPos().add(5), player.getPos().add(-5));
        List<Entity> entities = world.getOtherEntities(player, box);
        entities.removeIf(entity -> entity.getAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES) != null);
        if (entities.isEmpty()) entities.add(player);
        for (Entity victim : entities) {
            BoxOutlineState.add(victim.getBoundingBox(), 0xFFFF0000);
            if (world.getRegistryKey().equals(World.OVERWORLD) && victim instanceof ServerPlayerEntity serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, new SetTimeForwardAnimationStateS2CPacket(true));
                victim.setAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES, new TimeForward.VisualContext(TimeForward.ANIMATION_DURATION, player.getPos()));
            } else ModUtil.pocketTeleport(victim);
        }
        return NO_COOLDOWN_RESULT;
    }));

	public static final SorceryItem COMBUSTION_SORCERY = RegisterHelper.spell("combustion", settings -> new SorceryItem(settings, new Spell() {
		@Override
		public int hitEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, float potencyMultiplier, int cooldownDivisor) {
			if (!player.getItemCooldownManager().isCoolingDown(player.getStackInHand(hand))) {
				player.getWorld().createExplosion(player, target.getX(), target.getY(), target.getZ(), player.distanceTo(target) / 4, World.ExplosionSourceType.NONE);
				player.damage(world, world.getDamageSources().inFire(), 2);
				return 100;
			}
			return FAIL_RESULT;
		}

		@Override
		public int cast(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownDivisor) {
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
            return FAIL_RESULT;
		}

	}));
    public static final SorceryItem CURRENT_SORCERY = RegisterHelper.spell("current", settings -> new SorceryItem(settings, new Spell() {
		@Override
        public int cast(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownDivisor) {
            player.setCurrentHand(hand);
			return NO_COOLDOWN_RESULT;
        }

        @Override
        public int castTick(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownDivisor) {
			MultiVec3d lastLink = lastLinks.get(player);
			int maxDistance = 100;
			Vec3d raycast = player.raycast(maxDistance, 1, false).getPos();
			Vec3d eyePos = player.getEyePos();
			boolean failedResult = raycast.distanceTo(eyePos) >= maxDistance;
			if (lastLink == null) {
				if (failedResult) return FAIL_RESULT;
				lastLinks.put(player, new MultiVec3d(raycast, raycast));
			}else{
                MultiVec3d value = new MultiVec3d(lastLink.get(0), !failedResult? raycast : eyePos.add(player.getRotationVec(1).normalize().multiply(eyePos.distanceTo(lastLink.get(0)))));
				lastLinks.put(player, value);
				LinkedLightningPersistentState linkedLightnings = LinkedLightningPersistentState.get(world);
				linkedLightnings.remove(lastLink);
				linkedLightnings.add(value);
			}
			return NO_COOLDOWN_RESULT;
		}

        @Override
        public int castStop(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownDivisor) {
			lastLinks.remove(player);
			return 100;
        }
    }));
	public static final SorceryItem KINESIS_SORCERY = RegisterHelper.spell("kinesis", settings -> new SorceryItem(settings, new Spell() {
        private static final HashMap<Entity, List<Entity>> selectedEntities = new HashMap<>();

        @Override
        public int hitEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, float potencyMultiplier, int cooldownDivisor) {
            Vec3d vec3d = player.getPos().subtract(target.getPos()).normalize().multiply(2);
            if (player.isOnGround()) target.addVelocity(vec3d.multiply(-1).add(0, 0.25, 0));
            else player.addVelocity(vec3d);
            player.fallDistance = 0;
            player.velocityModified = true;
            return NO_COOLDOWN_RESULT;
        }

		@Override
        public int cast(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownDivisor) {
            player.setCurrentHand(hand);
            if (selectedEntities.get(player) == null){
                selectedEntities.put(player, StreamSupport.stream(world.iterateEntities().spliterator(), false)
                    .filter(entity -> player.getRotationVec(1).normalize().dotProduct(entity.getEyePos().subtract(player.getEyePos()).normalize()) >= Math.cos(KinesisInteractionRenderer.INTERACTION_RANGE_RADIANS)).toList()
                );
                return NO_COOLDOWN_RESULT;
            }
            return FAIL_RESULT;
        }

		@Override
        public int castStop(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownDivisor) {
            List<Entity> selectedEntities = this.selectedEntities.remove(player);
            if (selectedEntities != null && !selectedEntities.isEmpty()) {
                for (Entity selectedEntity : selectedEntities) {
					selectedEntity.setVelocity(player.getRotationVec(1).normalize().subtract(selectedEntity.getPos().subtract(player.getPos()).normalize()).normalize().multiply(2));
                    selectedEntity.velocityModified = true;
                }
                return NO_COOLDOWN_RESULT;
            }
            return FAIL_RESULT;
        }

		@Override
		public int castTick(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownDivisor) {
			if (world instanceof ServerWorld serverWorld) player.damage(serverWorld, ModDamageTypes.createDamageSource(world, ModDamageTypes.KINETIC_BACKLASH), 1);
			return NO_COOLDOWN_RESULT;
		}
    }));


	public static void init() {}
}
