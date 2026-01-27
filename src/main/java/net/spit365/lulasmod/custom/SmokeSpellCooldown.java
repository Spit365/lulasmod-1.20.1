package net.spit365.lulasmod.custom;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.spit365.lulasmod.mod.ModData;

import java.util.stream.StreamSupport;

public final class SmokeSpellCooldown {
    public static final int MAX_COOLDOWN = 800;

    public static boolean isCoolingDown(Entity entity){
        Integer cooldown = entity.getAttached(ModData.SMOKE_SPELL_COOLDOWN);
        if (cooldown == null) return false;
        return cooldown >= 0;
    }

    public static int getPercent(Entity entity){
        Integer cooldown = entity.getAttached(ModData.SMOKE_SPELL_COOLDOWN);
        if (cooldown == null) return 100;
        return 100 - Math.clamp(cooldown * 100 / MAX_COOLDOWN, 0, 100);
    }
    
    public static void apply(Entity entity, int cooldownDivisor){
        entity.setAttached(ModData.SMOKE_SPELL_COOLDOWN, MAX_COOLDOWN / cooldownDivisor);
    }

    public static void tick(ServerWorld world){
        StreamSupport.stream(world.iterateEntities().spliterator(), true).forEach(entity -> {
            Integer cooldown = entity.getAttached(ModData.SMOKE_SPELL_COOLDOWN);
            if (cooldown == null) return;
            if (cooldown <= 1) {
                entity.removeAttached(ModData.SMOKE_SPELL_COOLDOWN);
                return;
            }
            entity.setAttached(ModData.SMOKE_SPELL_COOLDOWN, cooldown - 1);
        });
    }
}
