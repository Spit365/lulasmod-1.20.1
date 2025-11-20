package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.spit365.lulasmod.effect.CushionedStatusEffect;
import net.spit365.lulasmod.util.RegisterHelper;

public class ModStatusEffects {
	public static final RegistryEntry<StatusEffect> CUSHIONED = RegisterHelper.statusEffect("cushioned", new CushionedStatusEffect());

	public static void init() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> builder.registerItemRecipe(Items.LINGERING_POTION, Items.NETHER_STAR, ModItems.VIAL));
    }
}
