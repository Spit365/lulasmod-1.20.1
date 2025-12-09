package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.mod.ModDamageSources;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.packet.SummonBleedS2CPacket;

import java.util.stream.StreamSupport;

public class Bleed {
    public static void tick(ServerWorld world){
        StreamSupport.stream(world.iterateEntities().spliterator(), true).filter(entity -> entity instanceof LivingEntity && entity.getAttached(ModData.BLEED_VALUE) != null).map(LivingEntity.class::cast).forEach(entity -> {
            Integer duration = entity.getAttached(ModData.BLEED_VALUE);
            if (duration == null) return;
            int threshold = Math.min((int) (Math.min(entity.getHealth(), entity.getMaxHealth()) * 60), 1200);
            if (duration > threshold) {
                duration -= threshold;
                entity.damage(world, ModDamageSources.bloodsucking(world), entity.getMaxHealth() * 0.15f + 10f);
                StreamSupport.stream(world.iterateEntities().spliterator(), true)
					.filter(target -> target.squaredDistanceTo(entity) < 1000000 && target instanceof ServerPlayerEntity)
					.forEach(player -> ServerPlayNetworking.send((ServerPlayerEntity) player, new SummonBleedS2CPacket(entity.getX(), entity.getY(), entity.getZ())));
            }
            duration--;
            entity.setAttached(ModData.BLEED_VALUE, duration);
        });
    }

	public static void apply(LivingEntity entity, int duration){
		Integer bleed = entity.getAttached(ModData.BLEED_VALUE);
		entity.setAttached(ModData.BLEED_VALUE, duration + (bleed != null? bleed : 0));
	}

	public static void summonParticles(Vec3d pos, ClientWorld world) {
		if (world != null) for (int i = 0; i < world.random.nextInt(4) + 6; i++)
			world.addParticleClient(ModParticles.BLOOD, pos.getX(), pos.getY() + 1, pos.getZ(), 1, 0, 1);
	}
}
