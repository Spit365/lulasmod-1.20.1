package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;

public class ModBlocks {
    public static final Block TESTBLOCK = registerBlock("testblock", new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));


    public static void registerModBlocks(){
        Lulasmod.LOGGER.info("Registering Mod Blocks for " + Lulasmod.MOD_ID);
    }
    private static Item registerBlocksItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(Lulasmod.MOD_ID, name), new BlockItem(block, new FabricItemSettings()));
    }
    private static Block registerBlock(String name, Block block){
        registerBlocksItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(Lulasmod.MOD_ID,name), block);
    }
}
