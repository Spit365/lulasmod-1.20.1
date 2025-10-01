package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.custom.state.LinkedLightningPersistentState;
import net.spit365.lulasmod.manager.MultiVec3d;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static net.spit365.lulasmod.mod.ModMethods.impaled;

public class ModServerTick {
	private static int impaledCounter = 0;
	private static final int CURRENT_UPDATE_RANGE = 1000000;

    public static void init(){
		ServerTickEvents.END_SERVER_TICK.register(input -> {
			for (ServerPlayerEntity player : input.getPlayerManager().getPlayerList()) {
				if (player.getCommandTags().contains("miner")) {
					BlockPos playerPos = player.getBlockPos();
					BlockPos closestPortal = null;
					for (BlockPos pos : BlockPos.stream(
						playerPos.add(-5, -5, -5),
						playerPos.add(5, 5, 5)
					).map(BlockPos::toImmutable).toList())
						if ((
							player.getWorld().getBlockState(pos).isOf(Blocks.END_PORTAL) ||
								player.getWorld().getBlockState(pos).isOf(Blocks.NETHER_PORTAL)) &&
							(closestPortal == null || pos.getSquaredDistance(playerPos) < closestPortal.getSquaredDistance(playerPos)
							)) closestPortal = pos;
					if (closestPortal != null) {
						ModMethods.outlineBox(Box.enclosing(closestPortal.add(-5, -5, -5), closestPortal.add(5, 5, 5)), player.getWorld(), ModParticles.GOLDEN_SHIMMER);
						player.setVelocity(player.getPos().subtract(Vec3d.ofCenter(closestPortal)).normalize());
						player.velocityModified = true;
					}
				}

				if(player.getMainHandStack().getItem() instanceof SpellHotbar item) ModMethods.sendSpellListPacket(player, item.getHotbarList(player));
				else if(player.getOffHandStack().getItem() instanceof SpellHotbar item) ModMethods.sendSpellListPacket(player, item.getHotbarList(player));

				Integer i = player.getAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
				if (i != null) {
					if (i > 0) player.setAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES, i -1);
					else {
						player.removeAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
						ModMethods.pocketTeleport(player);
					}
				}
			}

			impaledCounter++;
			for (ModMethods.ImpaledContext context : impaled) {
				LivingEntity victim = context.livingEntity();
				if (context.iterations() > 0 && victim.isAlive()) {
					if (victim instanceof EndermanEntity) victim.kill((ServerWorld) victim.getWorld());
					victim.setVelocity(0, 0, 0);
					if (impaledCounter >= context.intervalls()) {
						impaledCounter = 0;
						double radius = 5;
						Vec3d pos = new Vec3d(Math.random() * radius - radius / 2, Math.random() * radius - radius / 2, Math.random() * radius - radius / 2).normalize().multiply(radius).add(victim.getPos());
						context.player().setAttached(ModData.DAMAGE_DELAY, 0);
						victim.getWorld().spawnEntity(new ParticleProjectileEntity(
							victim.getWorld(), context.player(), pos, pos.subtract(victim.getEyePos()).multiply(-0.5), context.particle()));
						impaled.remove(context);
						impaled.add(new ModMethods.ImpaledContext(context, context.iterations() - 1, context.intervalls()));
					}
				} else {
					impaled.remove(context);
					context.player().removeAttached(ModData.DAMAGE_DELAY);
					victim.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 50));
				}
			}

			input.getWorlds().forEach(serverWorld -> StreamSupport.stream(serverWorld.iterateEntities().spliterator(), true).filter(entity -> entity instanceof LivingEntity && entity.getAttached(ModData.BLEED_VALUE) != null).map(LivingEntity.class::cast).forEach(entity -> {
				Integer duration = entity.getAttached(ModData.BLEED_VALUE);
				if (duration == null) return;
				int min = Math.min((int) (Math.min(entity.getHealth(), entity.getMaxHealth()) * 60) - 1, 1200);
				if (duration > min) {
					entity.setAttached(ModData.BLEED_VALUE, duration - min);
					entity.damage(serverWorld, ModDamageSources.BLOODSUCKING(entity), entity.getMaxHealth() * 0.15f + 10f);
					serverWorld.spawnParticles(ParticleTypes.EFFECT, entity.getX(), entity.getY(), entity.getZ(), 5, 1, 1, 1, 1);
				}
			}));

			for (ServerWorld serverWorld : input.getWorlds()){
				LinkedLightningPersistentState linkedLightningPersistentState = LinkedLightningPersistentState.get(serverWorld);
				Set<MultiVec3d> links = linkedLightningPersistentState.getLinks();

				outerloop:
				for (MultiVec3d multiVec3d : links) {
					if(multiVec3d.pairwiseSegments().allMatch(twoVec3d -> twoVec3d.start().distanceTo(twoVec3d.end()) < 0.5d)){
						linkedLightningPersistentState.remove(multiVec3d);
						break;
					}
					Vec3d[] laser = multiVec3d.stream().toArray(Vec3d[]::new);
					for (int i = 1; i < laser.length; i++) {
						List<Entity> otherEntities = serverWorld.getOtherEntities(null, new Box(laser[i - 1], laser[i]));
						if (!otherEntities.isEmpty()) {
							otherEntities.forEach(entity -> {
								serverWorld.playSound(entity, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.PLAYERS);
								entity.damage(serverWorld, serverWorld.getDamageSources().lightningBolt(), 10);
							});
							links.remove(multiVec3d);
							Set<LivingEntity> livingEntitySet = new HashSet<>();
							LinkedLightningPersistentState.lastLinks.entrySet().removeIf(entityMultiVec3dEntry -> {
								boolean result = entityMultiVec3dEntry.getValue().equals(multiVec3d);
								if (result && entityMultiVec3dEntry.getKey() instanceof LivingEntity livingEntity) livingEntitySet.add(livingEntity);
								return result;
							});
							livingEntitySet.forEach(LivingEntity::stopUsingItem);
							break outerloop;
						}
					}
				}

				serverWorld.getPlayers().forEach(serverPlayer ->
					ServerPlayNetworking.send(serverPlayer, new ModPackets.LightningLinkS2CPacket(
						links.stream().filter(multiVec3d ->
							Arrays.stream(multiVec3d.vec3ds()).anyMatch(vec3d -> serverPlayer.getPos().isInRange(vec3d, CURRENT_UPDATE_RANGE))
						).collect(Collectors.toSet())
					))
				);
			}
		});
    }
}