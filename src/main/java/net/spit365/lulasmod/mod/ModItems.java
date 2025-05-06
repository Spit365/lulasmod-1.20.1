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

    public static final Item MODIFIED_TNT       = register("modified_tnt",          new ModifiedTntItem(new Item.Settings().maxCount(16)));
    public static final Item SMOKE_BOMB         = register("smoke_bomb",            new SmokeBombItem(new Item.Settings().maxCount(16)));
    public static final Item HOME_BUTTON        = register("home_button",           new HomeButtonItem(new Item.Settings().maxCount(1).maxDamage(100)));
    public static final Item GOLDEN_TRIDENT     = register("golden_trident",        new GoldenTridentItem(new Item.Settings().maxCount(1).maxDamage(500)));
    public static final Item SHARP_TOME         = register("sharp_tome",            new SharpTomeItem(new Item.Settings().maxCount(1).maxDamage(640)));
    public static final Item LASCIVIOUSNESS     = register("lasciviousness",        new LasciviousnessItem(new Item.Settings().maxCount(1).maxDamage(500)));

    public static final Item HELLISH_SEAL       = register("hellish_seal",          new HellishSeal(new Item.Settings().maxCount(1)));
    public static final Item GOLDEN_SEAL        = register("golden_seal",           new GoldenSeal(new Item.Settings().maxCount(1)));
    public static final Item BLOODSUCKING_SEAL  = register("bloodsucking_seal",     new BloodsuckingSeal(new Item.Settings().maxCount(1)));

    private static Item register(String name, Item item) {
        CreativeTabItems.add(Identifier.of(Lulasmod.MOD_ID, name));
        return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
    }

    public static void init() {}
}