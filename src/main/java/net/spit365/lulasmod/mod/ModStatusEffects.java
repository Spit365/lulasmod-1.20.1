package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.registry.FabricPotionBrewingBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.Items;
import net.spit365.lulasmod.effect.CushionedStatusEffect;
import net.spit365.lulasmod.util.RegisterHelper;

public final class ModStatusEffects {
	public static final Holder<MobEffect> CUSHIONED = RegisterHelper.statusEffect("cushioned", new CushionedStatusEffect());

	public static void init() {
        FabricPotionBrewingBuilder.BUILD.register(builder -> builder.addContainerRecipe(Items.LINGERING_POTION, Items.NETHER_STAR, ModItems.VIAL));
    }
}
