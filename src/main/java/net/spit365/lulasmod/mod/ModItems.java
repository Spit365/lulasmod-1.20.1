package net.spit365.lulasmod.mod;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.item.*;

import java.util.LinkedList;
import java.util.List;

public class ModItems {
    public static final List<Identifier> ModItemList            = new LinkedList<>() {};
    public static final List<Identifier> IncantationItems  = new LinkedList<>() {};

    public static final Item MODIFIED_TNT                   = registerItem("modified_tnt",          new ModifiedTntItem(new Item.Settings().maxCount(16)),true);
    public static final Item SMOKE_BOMB                     = registerItem("smoke_bomb",            new SmokeBombItem(new Item.Settings().maxCount(16)),true);
    public static final Item HOME_BUTTON                    = registerItem("home_button",           new HomeButtonItem(new Item.Settings().maxCount(1)),true);
    public static final Item SHARP_TOME                     = registerItem("sharp_tome",            new SharpTomeItem(new Item.Settings().maxCount(1)),true);
    public static final Item HELLISH_SEAL                   = registerItem("hellish_seal",          new SealItem(new Item.Settings().maxCount(1)),true);

    public static final Item FLAME_INCANTATION              = registerItem("flame_inc",             new IncantationItem(new Item.Settings().maxCount(1)),false);
    public static final Item HOME_INCANTATION               = registerItem("home_inc",              new IncantationItem(new Item.Settings().maxCount(1)),false);
    public static final Item SMOKE_INCANTATION              = registerItem("smoke_inc",             new IncantationItem(new Item.Settings().maxCount(1)),false);
    public static final Item HIGHLIGHTER_INCANTATION        = registerItem("highlighter_inc",       new IncantationItem(new Item.Settings().maxCount(1)),false);
    public static final Item POCKET_INCANTATION             = registerItem("pocket_inc",            new IncantationItem(new Item.Settings().maxCount(1)),false);
    public static final Item DASH_INCANTATION               = registerItem("dash_inc",              new IncantationItem(new Item.Settings().maxCount(1)),false);

    private static Item registerItem(String name, Item item, Boolean showInCreativeTab) {
        if (showInCreativeTab) ModItemList.add(Identifier.of(Lulasmod.MOD_ID, name));
        if (item instanceof IncantationItem) IncantationItems.add(Identifier.of(Lulasmod.MOD_ID, name));
        return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
    }

    public static void init() {}
}