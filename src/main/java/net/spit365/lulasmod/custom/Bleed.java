package net.spit365.lulasmod.custom;

import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.spit365.lulasmod.mod.ModDamageSources;
import net.spit365.lulasmod.mod.ModData;

import java.util.stream.StreamSupport;

public class Bleed {
    public static void tick(ServerWorld serverWorld){
        StreamSupport.stream(serverWorld.iterateEntities().spliterator(), true).filter(entity -> entity instanceof LivingEntity && entity.getAttached(ModData.BLEED_VALUE) != null).map(LivingEntity.class::cast).forEach(entity -> {
            Integer duration = entity.getAttached(ModData.BLEED_VALUE);
            if (duration == null) return;
            int min = Math.min((int) (Math.min(entity.getHealth(), entity.getMaxHealth()) * 60) - 1, 1200);
            if (duration > min) {
                entity.setAttached(ModData.BLEED_VALUE, duration - min);
                entity.damage(serverWorld, ModDamageSources.bloodsucking(entity), entity.getMaxHealth() * 0.15f + 10f);
                serverWorld.spawnParticles(ParticleTypes.EFFECT, entity.getX(), entity.getY(), entity.getZ(), 5, 1, 1, 1, 1);
            }
        });
    }
}
