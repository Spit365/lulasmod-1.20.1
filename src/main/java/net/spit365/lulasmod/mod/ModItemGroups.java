package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import static net.spit365.lulasmod.mod.ModItems.CreativeTabItems;
import static net.spit365.lulasmod.mod.ModSpells.SpellTabItems;

public class ModItemGroups {
    public static final ItemGroup LULAS_GROUP = register("lulasmod_group", ModItems.SMOKE_BOMB, CreativeTabItems);
    public static final ItemGroup SPELLS_GROUP = register("lulasmod_spells", ModSpells.HOME_SPELL, SpellTabItems);

    private static @Nullable ItemGroup register(String name, Item item, List<Identifier> list) {
        return Registry.register(Registries.ITEM_GROUP, new Identifier(Lulasmod.MOD_ID, name), FabricItemGroup.builder().displayName(Text.translatable("item_group." + name)).icon(() -> new ItemStack(item)).entries((displayContext, entries) -> {
            for (Identifier id : list) entries.add(Registries.ITEM.get(id));
            System.gc();
        }).build());
    }

    public static  void init(){}
}
