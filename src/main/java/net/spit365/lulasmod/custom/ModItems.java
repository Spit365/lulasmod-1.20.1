package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

public class ModItems {

    public static final Item DRAGON_FIREBALL = registerItem("dragon_fireball", new Item(new Item.Settings()));
    public static final Item MODIFIED_TNT = registerItem("modified_tnt", new Item(new Item.Settings()));
    public static final Item HIGHLIGHTER = registerItem("highlighter", new Item(new Item.Settings()));
    public static final Item LIGHTNING_CRYSTAL = registerItem("lightning_crystal", new Item(new Item.Settings().maxCount(1)));
    public static final Item GRAVITATOR = registerItem("gravitator", new Item(new Item.Settings().maxCount(1)));
    public static final Item SMOKE_BOMB = registerItem("smoke_bomb", new Item(new Item.Settings().maxCount(16)));
    //public static final Item BOMB_ITEM = registerItem("bomb", new BombItem(new Item.Settings()));


    //public static final EntityType<BombEntity> BOMB_ENTITY = Registry.register(
           // Registries.ENTITY_TYPE,
            //new Identifier(Testi.MOD_ID, "bomb"),
            //EntityType.Builder.create(BombEntity::new, SpawnGroup.AMBIENT)
                   // .dimensions(EntityDimensions.fixed(0.75f, 0.75f))
                  //  .build());





    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(Lulasmod.MOD_ID, name), item);
    }

    public static void init() {
        Lulasmod.LOGGER.info("Registering Mod Items for " + Lulasmod.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
            entries.add(DRAGON_FIREBALL);
            entries.add(MODIFIED_TNT);
            entries.add(HIGHLIGHTER);
            entries.add(LIGHTNING_CRYSTAL);
            entries.add(GRAVITATOR);
            entries.add(SMOKE_BOMB);
            //entries.add(BOMB_ITEM);
        });
    }
}