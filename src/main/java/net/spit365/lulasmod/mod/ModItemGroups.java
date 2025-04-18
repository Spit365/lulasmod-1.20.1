package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

import static net.spit365.lulasmod.mod.ModItems.*;

public class ModItemGroups {
    public static final ItemGroup LULAS_GROUP = Registry.register(Registries.ITEM_GROUP, new Identifier(Lulasmod.MOD_ID, "lulas_group"), FabricItemGroup.builder().displayName(Text.literal(Lulasmod.MOD_ID)).icon(() -> new ItemStack(ModItems.SMOKE_BOMB)).entries((displayContext, entries) -> {
        for (Identifier id : ModItemList){entries.add(Registries.ITEM.get(id));}
    }).build());
    public static  void init(){}
}
