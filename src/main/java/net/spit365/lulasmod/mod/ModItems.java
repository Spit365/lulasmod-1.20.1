package net.spit365.lulasmod.mod;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

import java.util.LinkedList;
import java.util.List;

public class ModItems {
    public static       List<Identifier> ModItemList        = new LinkedList<>() {};

    public static final Item DRAGON_FIREBALL                = registerItem("dragon_fireball",       new Item(new Item.Settings().maxCount(16)), true);
    public static final Item MODIFIED_TNT                   = registerItem("modified_tnt",          new Item(new Item.Settings()),true);
    public static final Item HIGHLIGHTER                    = registerItem("highlighter",           new Item(new Item.Settings().maxCount(1)),true);
    public static final Item SMOKE_BOMB                     = registerItem("smoke_bomb",            new Item(new Item.Settings().maxCount(16)),true);
    public static final Item HOME_BUTTON                    = registerItem("home_button",           new Item(new Item.Settings().maxCount(1)),true);
    public static final Item HELLISH_SEAL                   = registerItem("hellish_seal",          new Item(new Item.Settings().maxCount(1)),true);

    public static final Item EMPTY_INCANTATION              = registerItem("empty_inc",             new Item(new Item.Settings().maxCount(1)),true);
    public static final Item LIGHTNING_CRYSTAL_INCANTATION  = registerItem("lightning_crystal_inc", new Item(new Item.Settings().maxCount(1)),false);
    public static final Item HOME_INCANTATION               = registerItem("home_inc",              new Item(new Item.Settings().maxCount(1)),false);
    public static final Item SMOKE_INCANTATION              = registerItem("smoke_inc",             new Item(new Item.Settings().maxCount(1)),false);

    private static Item registerItem(String name, Item item, Boolean showInCreativeTab) {
        if (showInCreativeTab) {ModItemList.add(Identifier.of(Lulasmod.MOD_ID, name));}
        return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
    }

    public static void init() {}
}