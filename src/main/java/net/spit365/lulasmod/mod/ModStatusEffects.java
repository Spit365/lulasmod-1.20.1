package net.spit365.lulasmod.mod;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.effect.CushionedStatusEffect;

public class ModStatusEffects {
    public static final StatusEffect CUSHIONED = register("cushioned", new CushionedStatusEffect());

    private static StatusEffect register(String id, StatusEffect entry) {
        return Registry.register(Registries.STATUS_EFFECT, new Identifier(Lulasmod.MOD_ID, id), entry);
    }

    public static void init(){}
}
