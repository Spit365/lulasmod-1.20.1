package net.spit365.lulasmod.mod;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.HellishSealItem;
import net.spit365.lulasmod.custom.item.*;

import java.util.LinkedList;
import java.util.List;

public class ModItems {
    public static       List<Identifier> ModItemList        = new LinkedList<>() {};

    public static final Item DRAGON_FIREBALL                = registerItem("dragon_fireball",       new DragonFireballItem(new Item.Settings().maxCount(16)), true);
    public static final Item MODIFIED_TNT                   = registerItem("modified_tnt",          new ModifiedTntItem(new Item.Settings().maxCount(16)),true);
    public static final Item SMOKE_BOMB                     = registerItem("smoke_bomb",            new SmokeBombItem(new Item.Settings().maxCount(16)),true);
    public static final Item HOME_BUTTON                    = registerItem("home_button",           new HomeButtonItem(new Item.Settings().maxCount(1)),true);
    public static final Item HELLISH_SEAL                   = registerItem("hellish_seal",          new HellishSealItem(new Item.Settings().maxCount(1)),true);

    public static final Item EMPTY_INCANTATION              = registerItem("empty_inc",             new Item(new Item.Settings().maxCount(1)),true);
    public static final Item LIGHTNING_CRYSTAL_INCANTATION  = registerItem("lightning_crystal_inc", new IncantationItem(new Item.Settings().maxCount(1)),false);
    public static final Item HOME_INCANTATION               = registerItem("home_inc",              new IncantationItem(new Item.Settings().maxCount(1)),false);
    public static final Item SMOKE_INCANTATION              = registerItem("smoke_inc",             new IncantationItem(new Item.Settings().maxCount(1)),false);
    public static final Item HIGHLIGHTER_INCANTATION        = registerItem("highlighter_inc",       new IncantationItem(new Item.Settings().maxCount(1)),false);
    public static final Item POCKET_INCANTATION             = registerItem("pocket_inc",            new IncantationItem(new Item.Settings().maxCount(1)),false);

    private static Item registerItem(String name, Item item, Boolean showInCreativeTab) {
        if (showInCreativeTab) {ModItemList.add(Identifier.of(Lulasmod.MOD_ID, name));}
        return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
    }

    public static void init() {}
}