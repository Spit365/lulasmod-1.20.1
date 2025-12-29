package net.spit365.lulasmod.mod;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.item.BlockItem;
import net.spit365.lulasmod.block.SpellPedestalBlock;
import net.spit365.lulasmod.util.RegisterHelper;

public class ModBlocks {
	public record BlockAndItem<T extends Block>(T block, BlockItem item){}
	public static final BlockAndItem<SpellPedestalBlock> SPELL_PEDESTAL = RegisterHelper.block(
		"spell_pedestal",
		SpellPedestalBlock::new,
		AbstractBlock.Settings.create()
			.mapColor(MapColor.STONE_GRAY)
			.strength(-1.0F, Float.MAX_VALUE)
			.dropsNothing()
			.allowsSpawning(Blocks::never));

	public static void init() {}
}
