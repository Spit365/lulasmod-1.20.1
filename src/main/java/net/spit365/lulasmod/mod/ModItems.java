package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolMaterial;
import net.minecraft.potion.Potion;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.Demon;
import net.spit365.lulasmod.item.*;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.util.RegisterHelper;

import java.util.LinkedList;
import java.util.List;

public class ModItems {
	public static final List<ItemStack> MainTabItems = new LinkedList<>();

	public static final SealItem SEAL = RegisterHelper.item("seal", settings -> new SealItem(settings, entity -> true, 1, 1), new Item.Settings().maxCount(1));
	public static final SealItem HELLISH_SEAL = RegisterHelper.item("hellish_seal", settings -> new SealItem(settings, Demon::isDemon, 2, 1), new Item.Settings().maxCount(1).fireproof());
	public static final SealItem GOLDEN_SEAL = RegisterHelper.item("golden_seal", settings -> new SealItem(settings, entity -> entity instanceof ServerPlayerEntity player && (player.isCreative() || player.experienceLevel > 0 || player.experienceProgress > 0f), (entity, cooldown) -> {if (entity instanceof ServerPlayerEntity player && !player.isCreative()) player.addExperience((int) Math.ceil(cooldown / -20d));}, 1, 2), new Item.Settings().maxCount(1));
	public static final SealItem BLOODSUCKING_SEAL = RegisterHelper.item("bloodsucking_seal", settings -> new SealItem(settings, entity -> true, (entity, cooldown) -> {if (!Demon.isDemon(entity)) Bleed.apply(entity, cooldown * 5);}, 2, 1), new Item.Settings().maxCount(1));

	public static final ModifiedTntItem MODIFIED_TNT = RegisterHelper.item("modified_tnt", ModifiedTntItem::new, new Item.Settings().maxCount(16));
	public static final SmokeBombItem SMOKE_BOMB = RegisterHelper.item("smoke_bomb", SmokeBombItem::new, new Item.Settings().maxCount(16));
	public static final HomeButtonItem HOME_BUTTON = RegisterHelper.item("home_button", HomeButtonItem::new, new Item.Settings().maxCount(1).maxDamage(100));
	public static final GoldenTridentItem GOLDEN_TRIDENT = RegisterHelper.item("golden_trident", GoldenTridentItem::new, new Item.Settings().maxCount(1).maxDamage(500));
	public static final SharpTomeItem SHARP_TOME = RegisterHelper.item("sharp_tome", SharpTomeItem::new, new Item.Settings().maxCount(1).maxDamage(640));
	public static final SinfulItem SINFUL = RegisterHelper.item("sinful", SinfulItem::new, new Item.Settings().sword(ToolMaterial.NETHERITE, 3, -2.4F).fireproof().maxCount(1).maxDamage(2500));
	public static final SpellBookItem SPELL_BOOK = RegisterHelper.item("spell_book", SpellBookItem::new, new Item.Settings().maxCount(1));
    public static final NeedleSwordItem NEEDLE_SWORD = RegisterHelper.item("needle_sword", NeedleSwordItem::new, new Item.Settings().sword(ToolMaterial.NETHERITE, 3, 0).maxCount(1).maxDamage(2500).fireproof());
    public static final VialItem VIAL = RegisterHelper.item("vial", VialItem::new, new Item.Settings().maxCount(16), (item, itemStacks) -> {
		for (Potion potion : Registries.POTION) {
			ItemStack stack = new ItemStack(item);
			stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Registries.POTION.getEntry(potion)));
			itemStacks.add(stack);
		}
	});

    public static final Item NEEDLE_HEAD = RegisterHelper.item("needle_head", Item::new, new Item.Settings().fireproof());

	public static final ItemGroup LULAS_GROUP = RegisterHelper.itemGroup("lulasmod_main", ModItems.SMOKE_BOMB, ModItems.MainTabItems);
	public static final ItemGroup SPELLS_GROUP = RegisterHelper.itemGroup("lulasmod_spells", ModSpells.HOME_SPELL, ModSpells.SpellTabItems);

	public static void init() {
		ItemTooltipCallback.EVENT.register((stack, tooltipContext, tooltipType, list) -> {
			switch (stack.getItem()){
				case SpellBookItem ignored -> {
					List<Identifier> spells = stack.get(ModData.SPELL_BOOK_SPELLS);
					if (spells != null && !spells.isEmpty()) spells.forEach(id ->
						list.add(Registries.ITEM.get(id).getName()));
				}
				case SpellItem item -> list.add(Text.translatable("spell." + Registries.ITEM.getId(item).getNamespace() + ".tooltip." + Registries.ITEM.getId(item).getPath()));
                default -> {}
            }
		});
	}
}
