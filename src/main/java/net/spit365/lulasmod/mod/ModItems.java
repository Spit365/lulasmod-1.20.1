package net.spit365.lulasmod.mod;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.item.*;
import net.spit365.lulasmod.manager.RegisterHelper;

import java.util.LinkedList;
import java.util.List;

public class ModItems {
	public static final List<Identifier> CreativeTabItems = new LinkedList<>();

	public static final Item MODIFIED_TNT = RegisterHelper.item("modified_tnt", new ModifiedTntItem());
	public static final Item SMOKE_BOMB = RegisterHelper.item("smoke_bomb", new SmokeBombItem());
	public static final Item HOME_BUTTON = RegisterHelper.item("home_button", new HomeButtonItem());
	public static final Item GOLDEN_TRIDENT = RegisterHelper.item("golden_trident", new GoldenTridentItem());
	public static final Item SHARP_TOME = RegisterHelper.item("sharp_tome", new SharpTomeItem());
	public static final Item SINFUL = RegisterHelper.item("sinful", new SinfulItem());
	public static final Item SPELL_BOOK = RegisterHelper.item("spell_book", new SpellBookItem());

	public static final Item SEAL = RegisterHelper.item("seal", new SealItem(entity -> true, 1, 1));
	public static final Item HELLISH_SEAL = RegisterHelper.item("hellish_seal", new SealItem(entity -> entity.getCommandTags().contains("tailed"), 2, 1));
	public static final Item GOLDEN_SEAL = RegisterHelper.item("golden_seal", new SealItem(entity -> {
		if (entity instanceof ServerPlayerEntity player && !player.isCreative()) {
			if (player.experienceLevel <= 0 && player.experienceProgress <= 0f) return false;
			player.addExperience(-1);
		}
		return true;
	}, 1, 2));
	public static final Item BLOODSUCKING_SEAL = RegisterHelper.item("bloodsucking_seal", new SealItem(entity -> {
		ModMethods.applyBleed(entity, 100);
		return true;
	}, 2, 1));

	public static final List<Item> tailedExclusive = List.of(ModItems.HELLISH_SEAL, ModSpells.SLASH_SPELL, ModSpells.BLOOD_SPELL, ModSpells.POCKET_SPELL);

	public static final ItemGroup LULAS_GROUP = RegisterHelper.itemGroup("lulasmod_group", ModItems.SMOKE_BOMB, ModItems.CreativeTabItems);
	public static final ItemGroup SPELLS_GROUP = RegisterHelper.itemGroup("lulasmod_spells", ModSpells.HOME_SPELL, ModSpells.SpellTabItems);

	public static void init() {}
}
