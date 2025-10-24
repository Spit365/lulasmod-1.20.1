package net.spit365.lulasmod.mod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.item.*;
import net.spit365.lulasmod.manager.RegisterHelper;

import java.util.LinkedList;
import java.util.List;

public class ModItems {
	public static final List<Identifier> CreativeTabItems = new LinkedList<>();

	public static final Item MODIFIED_TNT = RegisterHelper.item("modified_tnt", ModifiedTntItem::new, new Item.Settings().maxCount(16));
	public static final Item SMOKE_BOMB = RegisterHelper.item("smoke_bomb", SmokeBombItem::new, new Item.Settings().maxCount(16));
	public static final Item HOME_BUTTON = RegisterHelper.item("home_button", HomeButtonItem::new, new Item.Settings().maxCount(1).maxDamage(100));
	public static final Item GOLDEN_TRIDENT = RegisterHelper.item("golden_trident", GoldenTridentItem::new, new Item.Settings().maxCount(1).maxDamage(500));
	public static final Item SHARP_TOME = RegisterHelper.item("sharp_tome", SharpTomeItem::new, new Item.Settings().maxCount(1).maxDamage(640));
	public static final Item SINFUL = RegisterHelper.item("sinful", SinfulItem::new, new Item.Settings().sword(ToolMaterial.NETHERITE, 3, -2.4F).fireproof().maxCount(1).maxDamage(2500));
	public static final Item SPELL_BOOK = RegisterHelper.item("spell_book", SpellBookItem::new, new Item.Settings().maxCount(1));
    public static final Item NEEDLE_SWORD = RegisterHelper.item("needle_sword", NeedleSwordItem::new, new Item.Settings().sword(ToolMaterial.NETHERITE, 3, 0).maxCount(1).maxDamage(2500).fireproof());

    public static final Item NEEDLE_HEAD = RegisterHelper.item("needle_head", Item::new, new Item.Settings().fireproof());

	public static final Item SEAL = RegisterHelper.item("seal", settings -> new SealItem(settings, entity -> true, 1, 1), new Item.Settings().maxCount(1));
	public static final Item HELLISH_SEAL = RegisterHelper.item("hellish_seal", settings -> new SealItem(settings, entity -> entity.getCommandTags().contains("tailed"), 2, 1), new Item.Settings().maxCount(1).fireproof());
	public static final Item GOLDEN_SEAL = RegisterHelper.item("golden_seal", settings -> new SealItem(settings, entity -> {
		if (entity instanceof ServerPlayerEntity player && !player.isCreative()) {
			if (player.experienceLevel <= 0 && player.experienceProgress <= 0f) return false;
			player.addExperience(-1);
		}
		return true;
	}, 1, 2), new Item.Settings().maxCount(1));
	public static final Item BLOODSUCKING_SEAL = RegisterHelper.item("bloodsucking_seal", settings -> new SealItem(settings, entity -> {
		ModMethods.applyBleed(entity, 100);
		return true;
	}, 2, 1), new Item.Settings().maxCount(1));

	public static final ItemGroup LULAS_GROUP = RegisterHelper.itemGroup("lulasmod_group", ModItems.SMOKE_BOMB, ModItems.CreativeTabItems);
	public static final ItemGroup SPELLS_GROUP = RegisterHelper.itemGroup("lulasmod_spells", ModSpells.HOME_SPELL, ModSpells.SpellTabItems);

	public static void init() {}
}
