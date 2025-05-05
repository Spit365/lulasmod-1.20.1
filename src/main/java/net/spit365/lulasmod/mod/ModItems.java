package net.spit365.lulasmod.mod;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.item.*;
import net.spit365.lulasmod.custom.item.seal.*;

import java.util.LinkedList;
import java.util.List;

public class ModItems {
    public static final List<Identifier> CreativeTabItems = new LinkedList<>() {};
    public static final List<Identifier> IncantationItems  = new LinkedList<>() {};

    public static final Item MODIFIED_TNT       = register("modified_tnt",          new ModifiedTntItem(new Item.Settings().maxCount(16)),true);
    public static final Item SMOKE_BOMB         = register("smoke_bomb",            new SmokeBombItem(new Item.Settings().maxCount(16)),true);
    public static final Item HOME_BUTTON        = register("home_button",           new HomeButtonItem(new Item.Settings().maxCount(1).maxDamage(100)),true);
    public static final Item GOLDEN_TRIDENT     = register("golden_trident",        new GoldenTridentItem(new Item.Settings().maxCount(1).maxDamage(500)), true);
    public static final Item SHARP_TOME         = register("sharp_tome",            new SharpTomeItem(new Item.Settings().maxCount(1).maxDamage(640)),true);
    public static final Item LASCIVIOUSNESS     = register("lasciviousness",        new LasciviousnessItem(new Item.Settings().maxCount(1).maxDamage(500)), true);

    public static final Item HELLISH_SEAL       = register("hellish_seal",          new HellishSeal(new Item.Settings().maxCount(1)),true);
    public static final Item GOLDEN_SEAL        = register("golden_seal",           new GoldenSeal(new Item.Settings().maxCount(1)),true);
    public static final Item BLOODSUCKING_SEAL  = register("bloodsucking_seal",     new BloodsuckingSeal(new Item.Settings().maxCount(1)),true);

    public static final Item BLOOD_FLAME_SPELL  = register("treachery_judecca",    new SpellItem(new Item.Settings().maxCount(1)),false);
    public static final Item FIRE_SPELL         = register("malignity",              new SpellItem(new Item.Settings().maxCount(1)),false);
    public static final Item DASH_SPELL         = register("purloining",              new SpellItem(new Item.Settings().maxCount(1)),false);
    public static final Item SMOKE_SPELL        = register("guile",             new SpellItem(new Item.Settings().maxCount(1)),false);
    public static final Item HEAL_SPELL         = register("appeasing",              new SpellItem(new Item.Settings().maxCount(1)),false);
    public static final Item BLOOD_SPELL        = register("emulations",              new SpellItem(new Item.Settings().maxCount(1)),false);
    public static final Item HOME_SPELL         = register("wickedness",              new SpellItem(new Item.Settings().maxCount(1)),false);
    public static final Item POCKET_SPELL       = register("heresies",            new SpellItem(new Item.Settings().maxCount(1)),false);
    public static final Item HIGHLIGHTER_SPELL  = register("highlighter_inc",       new SpellItem(new Item.Settings().maxCount(1)),true);

    private static Item register(String name, Item item, Boolean showInCreativeTab) {
        if (showInCreativeTab) CreativeTabItems.add(Identifier.of(Lulasmod.MOD_ID, name));
        if (item instanceof SpellItem) IncantationItems.add(Identifier.of(Lulasmod.MOD_ID, name));
        return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
    }

    public static void init() {}
}