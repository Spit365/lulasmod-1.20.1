package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.world.entity.player.Player;
import net.spit365.lulasmod.mod.ModData;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class Presence {
    private static final Set<Presence> registeredPresences = new HashSet<>();

    public final int levelRequirement;

    private Presence(int levelRequirement) {
        this.levelRequirement = levelRequirement;
    }

    public boolean has(Player player) {
        Integer playerLevel = player.getAttached(ModData.PRESENCE_LEVEL);
        return playerLevel != null && playerLevel >= this.levelRequirement;
    }

    public static Presence register(int levelRequirement) {
        Presence presence = new Presence(levelRequirement);
        registeredPresences.add(presence);
        return presence;
    }

    public static Set<Presence> getWithLevelRequirement(Integer levelRequirement) {
        if (levelRequirement == null) return new HashSet<>();
        return registeredPresences.stream().filter(presence -> presence.levelRequirement >= levelRequirement).collect(Collectors.toSet());
    }

    public static void init() {
        ServerLivingEntityEvents.AFTER_DEATH.register((victim, damageSource) -> {
            if (victim instanceof Player) {
                if (damageSource.getEntity() instanceof Player attacker)
                    attacker.setAttached(ModData.PRESENCE_LEVEL,
                        attacker.getAttachedOrElse(ModData.PRESENCE_LEVEL, 0)
                        + victim.getAttachedOrElse(ModData.PRESENCE_LEVEL, 0)
                        + 1
                    );
                victim.removeAttached(ModData.PRESENCE_LEVEL);
            }
        });
    }
}
