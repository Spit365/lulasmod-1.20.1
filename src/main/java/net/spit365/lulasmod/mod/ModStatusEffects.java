package net.spit365.lulasmod.mod;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;
import net.spit365.lulasmod.effect.CushionedStatusEffect;
import net.spit365.lulasmod.util.RegisterHelper;

public class ModStatusEffects {
	public static final RegistryEntry<StatusEffect> CUSHIONED = RegisterHelper.statusEffect("cushioned", new CushionedStatusEffect());

	public static void init() {}
}
