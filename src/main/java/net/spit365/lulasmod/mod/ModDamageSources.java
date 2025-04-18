package net.spit365.lulasmod.mod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;


public class ModDamageSources {
    public static DamageSource ETERNAL_WINTER(Entity attacker) {
        RegistryEntry<DamageType> damageType = attacker.getWorld().getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(ModDamageSources.ETERNAL_WINTER())
                .orElseThrow();
        return new DamageSource(damageType);
    }

    public static RegistryKey<DamageType> ETERNAL_WINTER() {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Lulasmod.MOD_ID, "eternal_winter"));
    }
}
