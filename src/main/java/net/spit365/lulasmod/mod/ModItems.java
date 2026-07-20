package net.spit365.lulasmod.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.custom.Shimmer;
import net.spit365.lulasmod.item.*;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.util.RegisterHelper;

import java.util.LinkedList;
import java.util.List;

public final class ModItems {
	public static final List<ItemStack> MainTabItems = new LinkedList<>();

	public static final SealItem SEAL = RegisterHelper.item("seal", settings -> new SealItem(settings, entity -> true, 1, 1), new Item.Properties().stacksTo(1));
	public static final SealItem HELLISH_SEAL = RegisterHelper.item("hellish_seal", settings -> new SealItem(settings, Demon::isDemon, 2, 1), new Item.Properties().stacksTo(1).fireResistant());
	public static final SealItem GOLDEN_SEAL = RegisterHelper.item("golden_seal", settings -> new SealItem(settings, entity -> entity instanceof ServerPlayer player && (player.isCreative() || player.experienceLevel > 0 || player.experienceProgress > 0f), (entity, cooldown) -> {if (entity instanceof ServerPlayer player && !player.isCreative()) player.giveExperiencePoints((int) Math.ceil(cooldown / -20d));}, 1, 2), new Item.Properties().stacksTo(1));
	public static final SealItem BLOODSUCKING_SEAL = RegisterHelper.item("bloodsucking_seal", settings -> new SealItem(settings, entity -> true, (entity, cooldown) -> Bleed.apply(entity, cooldown * 5), 2, 1), new Item.Properties().stacksTo(1));

	public static final ModifiedTntItem MODIFIED_TNT = RegisterHelper.item("modified_tnt", ModifiedTntItem::new, new Item.Properties().stacksTo(16));
	public static final SmokeBombItem SMOKE_BOMB = RegisterHelper.item("smoke_bomb", SmokeBombItem::new, new Item.Properties().stacksTo(16));
	public static final HomeButtonItem HOME_BUTTON = RegisterHelper.item("home_button", HomeButtonItem::new, new Item.Properties().stacksTo(1).durability(100));
	public static final GoldenTridentItem GOLDEN_TRIDENT = RegisterHelper.item("golden_trident", GoldenTridentItem::new, new Item.Properties().stacksTo(1).durability(500));
	public static final SharpTomeItem SHARP_TOME = RegisterHelper.item("sharp_tome", SharpTomeItem::new, new Item.Properties().stacksTo(1).durability(640));
	public static final SinfulItem SINFUL = RegisterHelper.item("sinful", SinfulItem::new, new Item.Properties().sword(ToolMaterial.NETHERITE, 3, -2.4F).fireResistant().stacksTo(1).durability(2500));
	public static final SpellBookItem SPELL_BOOK = RegisterHelper.item("spell_book", SpellBookItem::new, new Item.Properties().stacksTo(1));
    public static final NeedleSwordItem NEEDLE_SWORD = RegisterHelper.item("needle_sword", NeedleSwordItem::new, new Item.Properties().sword(ToolMaterial.NETHERITE, 3, 0).stacksTo(1).durability(2500).fireResistant());
	public static final ShimmerSyringeItem SHIMMER_SYRINGE = RegisterHelper.item("shimmer_syringe", ShimmerSyringeItem::new, new Item.Properties().stacksTo(16), (item, itemStacks) -> {
		for (Shimmer.Variant variant : Shimmer.Variant.values()) {
			ItemStack stack = new ItemStack(item);
			stack.set(ModData.SHIMMER_VARIANT, variant);
			itemStacks.add(stack);
		}
	});
    public static final VialItem VIAL = RegisterHelper.item("vial", VialItem::new, new Item.Properties().stacksTo(16), (item, itemStacks) -> {
		for (Potion potion : BuiltInRegistries.POTION) {
			ItemStack stack = new ItemStack(item);
			stack.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potion)));
			itemStacks.add(stack);
		}
	});

    public static final Item NEEDLE_HEAD = RegisterHelper.item("needle_head", Item::new, new Item.Properties().fireResistant());

	public static final CreativeModeTab LULAS_GROUP = RegisterHelper.itemGroup("lulasmod_main", ModItems.SMOKE_BOMB, ModItems.MainTabItems);
	public static final CreativeModeTab SPELLS_GROUP = RegisterHelper.itemGroup("lulasmod_spells", ModSpells.HOME_SPELL, ModSpells.SpellTabItems);

	public static void init() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
			switch (stack.getItem()) {
				case SpellBookItem ignored -> {
					List<ResourceLocation> spells = stack.get(ModData.SPELL_BOOK_SPELLS);
					if (spells != null && !spells.isEmpty()) spells.forEach(id ->
						list.add(BuiltInRegistries.ITEM.getValue(id).getName()));
				}
				case SpellItem item -> list.add(Component.translatable("spell." + BuiltInRegistries.ITEM.getKey(item).getNamespace() + ".tooltip." + BuiltInRegistries.ITEM.getKey(item).getPath()));
                default -> {}
            }
		});
	}
}
