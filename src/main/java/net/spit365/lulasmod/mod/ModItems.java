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
    public static final List<Identifier> ModItemList       = new LinkedList<>() {};
    public static final List<Identifier> IncantationItems  = new LinkedList<>() {};

    public static final Item MODIFIED_TNT                   = register("modified_tnt",          new ModifiedTntItem(new Item.Settings().maxCount(16)),true);
    public static final Item SMOKE_BOMB                     = register("smoke_bomb",            new SmokeBombItem(new Item.Settings().maxCount(16)),true);
    public static final Item HOME_BUTTON                    = register("home_button",           new HomeButtonItem(new Item.Settings().maxCount(1).maxDamage(100)),true);
    public static final Item GOLDEN_TRIDENT                 = register("golden_trident",        new GoldenTridentItem(new Item.Settings().maxCount(1).maxDamage(500)), true);
    public static final Item SHARP_TOME                     = register("sharp_tome",            new SharpTomeItem(new Item.Settings().maxCount(1).maxDamage(640)),true);
    public static final Item LASCIVIOUSNESS                 = register("lasciviousness",        new LasciviousnessItem(new Item.Settings().maxCount(1).maxDamage(500)), true);

    public static final Item HELLISH_SEAL                   = register("hellish_seal",          new HellishSeal(new Item.Settings().maxCount(1)),true);
    public static final Item GOLDEN_SEAL                    = register("golden_seal",           new GoldenSeal(new Item.Settings().maxCount(1)),true);
    public static final Item BLOODSUCKING_SEAL              = register("bloodsucking_seal",     new BloodsuckingSeal(new Item.Settings().maxCount(1)),true);
    public static final Item CREATIVE_SEAL                  = register("creative_seal",         new CreativeSeal(new Item.Settings().maxCount(1)),true);

    public static final Item FROST_INCANTATION              = register("eternal_winter_inc",    new IncantationItem(new Item.Settings().maxCount(1), "eternal_winter"),false);
    public static final Item HOME_INCANTATION               = register("home_inc",              new IncantationItem(new Item.Settings().maxCount(1), "remembrance_of_a_safe_haven"),false);
    public static final Item SMOKE_INCANTATION              = register("smoke_inc",             new IncantationItem(new Item.Settings().maxCount(1), "assassin's_approach"),false);
    public static final Item HIGHLIGHTER_INCANTATION        = register("highlighter_inc",       new IncantationItem(new Item.Settings().maxCount(1), "highlighter"),false);
    public static final Item POCKET_INCANTATION             = register("pocket_inc",            new IncantationItem(new Item.Settings().maxCount(1), "realm_of_the_alternates"),false);
    public static final Item DASH_INCANTATION               = register("dash_inc",              new IncantationItem(new Item.Settings().maxCount(1), "swift_withdrawal"),false);
    public static final Item FIRE_INCANTATION               = register("fire_inc",              new IncantationItem(new Item.Settings().maxCount(1), "flame_sling"),false);
    public static final Item HEAL_INCANTATION               = register("heal_inc",              new IncantationItem(new Item.Settings().maxCount(1), "curing_artifact"),false);

    private static Item register(String name, Item item, Boolean showInCreativeTab) {
        if (showInCreativeTab) ModItemList.add(Identifier.of(Lulasmod.MOD_ID, name));
        if (item instanceof IncantationItem) IncantationItems.add(Identifier.of(Lulasmod.MOD_ID, name));
        return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
    }

    public static void init() {}
}