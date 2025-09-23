package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.Server;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.custom.entity.ParticleProjectileEntity;
import net.spit365.lulasmod.manager.TagManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static net.spit365.lulasmod.mod.ModMethods.impaled;

public class ModServerEvents {
    @SuppressWarnings("deprecation")
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
							player.getWorld().getBlockState(pos).isOf(net.minecraft.block.Blocks.END_PORTAL) ||
								player.getWorld().getBlockState(pos).isOf(net.minecraft.block.Blocks.NETHER_PORTAL)) &&
							(closestPortal == null || pos.getSquaredDistance(playerPos) < closestPortal.getSquaredDistance(playerPos)
							)) closestPortal = pos;
					if (closestPortal != null) {
						ModMethods.outlineBox(Box.enclosing(closestPortal.add(-5, -5, -5), closestPortal.add(5, 5, 5)), player.getWorld(), ModClient.Particles.GOLDEN_SHIMMER);
						player.setVelocity(player.getPos().subtract(Vec3d.ofCenter(closestPortal)).normalize());
						player.velocityModified = true;
					}
				}
			}

			for (ServerPlayerEntity player : input.getPlayerManager().getPlayerList()) {
				if(player.getMainHandStack().getItem() instanceof SpellHotbar item) ModMethods.sendSpellListPacket(player, item.displayList(player));
				else if(player.getOffHandStack().getItem() instanceof SpellHotbar item) ModMethods.sendSpellListPacket(player, item.displayList(player));
			}

			impaledCounter++;
			for (ImpaledContext context : impaled) {
				LivingEntity victim = context.livingEntity;
				if (context.iterations > 0 && victim.isAlive()) {
					if (victim instanceof EndermanEntity) victim.kill((ServerWorld) victim.getWorld());
					victim.setVelocity(0, 0, 0);
					if (impaledCounter >= 25) {
						impaledCounter = 0;
						double radius = 5;
						Vec3d pos = new Vec3d(Math.random() * radius - radius / 2, Math.random() * radius - radius / 2, Math.random() * radius - radius / 2).normalize().multiply(radius).add(victim.getPos());
						TagManager.put(context.player, ModServer.TagCategories.DAMAGE_DELAY, Identifier.of(Server.MOD_ID, "0"));
						victim.getWorld().spawnEntity(new ParticleProjectileEntity(
							victim.getWorld(), context.player, pos, pos.subtract(victim.getPos()).multiply(-0.5), context.particle, context.item));
						impaled.remove(context);
						impaled.add(new ImpaledContext(context, context.iterations - 1, context.item));
					}
				} else {
					impaled.remove(context);
					TagManager.remove(context.player, ModServer.TagCategories.DAMAGE_DELAY);
					victim.addStatusEffect(new StatusEffectInstance(net.minecraft.entity.effect.StatusEffects.SLOW_FALLING, 50));
				}
			}

			sporesCounter--;
			Map<Integer, String> tailedPlayers = new HashMap<>();
			input.getPlayerManager().getPlayerList().forEach(player -> {
				if (player.getCommandTags().contains("tailed")) {
					tailedPlayers.put(tailedPlayers.size(), player.getUuidAsString());
					if (sporesCounter <= 0 && player.getWorld() instanceof ServerWorld world)
						world.spawnParticles(ParticleTypes.CRIMSON_SPORE, player.getX(), player.getY() +1, player.getZ(), player.getRandom().nextBetweenExclusive(2, 4), 0, 0, 0, 0);
				}});
			if (sporesCounter <= 0) sporesCounter = new Random().nextInt(30, 60);
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeMap(tailedPlayers, PacketByteBuf::writeInt, PacketByteBuf::writeString);
			for (ServerPlayerEntity player : input.getPlayerManager().getPlayerList())
				ServerPlayNetworking.send(player, ModServer.Packets.TAILED_PLAYER_LIST, buf);

			for (ServerPlayerEntity player : input.getPlayerManager().getPlayerList()){
				Identifier read = TagManager.read(player, ModServer.TagCategories.TIME_FORWARD_ANIMATION_FRAMES);
				if (read != null) {
					int i = Integer.parseInt(read.getPath());
					if (i > 0) TagManager.put(player, ModServer.TagCategories.TIME_FORWARD_ANIMATION_FRAMES, Identifier.of(Server.MOD_ID, String.valueOf(i -1)));
					else {
						TagManager.remove(player, ModServer.TagCategories.TIME_FORWARD_ANIMATION_FRAMES);
						ModMethods.pocketTeleport(player);
					}
				}
			}
		});
        ServerPlayNetworking.registerGlobalReceiver(ModServer.Packets.CYCLE_PLAYER_SPELL, (a, player, b, c, d) -> {
            if (player.getMainHandStack().getItem() instanceof SpellHotbar item) item.cycleList(player);
            else if (player.getOffHandStack().getItem() instanceof SpellHotbar item) item.cycleList(player);
        });
    }

	private static int sporesCounter = 0;
	private static int impaledCounter = 0;
	public record ImpaledContext(PlayerEntity player, LivingEntity livingEntity, ParticleEffect particle, Integer iterations, ItemStack item) {
		public ImpaledContext(ImpaledContext context, Integer iterations, ItemStack item) {
			this(context.player(), context.livingEntity(), context.particle(), iterations, item);
		}
	}

}