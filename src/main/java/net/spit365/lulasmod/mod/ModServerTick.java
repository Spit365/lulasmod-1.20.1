package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;

import java.util.List;
import java.util.stream.StreamSupport;

import static net.spit365.lulasmod.mod.ModMethods.impaled;

public class ModServerTick {
	private static int impaledCounter = 0;

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

				List<Identifier> list = player.getAttached(ModData.EQUIPPED_SPELLS);
				if (list != null) Lulasmod.LOGGER.warn(list.toString());

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
					if (impaledCounter >= 25) {
						impaledCounter = 0;
						double radius = 5;
						Vec3d pos = new Vec3d(Math.random() * radius - radius / 2, Math.random() * radius - radius / 2, Math.random() * radius - radius / 2).normalize().multiply(radius).add(victim.getPos());
						context.player().setAttached(ModData.DAMAGE_DELAY, 0);
						victim.getWorld().spawnEntity(new ParticleProjectileEntity(
							victim.getWorld(), context.player(), pos, pos.subtract(victim.getPos()).multiply(-0.5), context.particle(), context.item()));
						impaled.remove(context);
						impaled.add(new ModMethods.ImpaledContext(context, context.iterations() - 1, context.item()));
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

		});
    }
}