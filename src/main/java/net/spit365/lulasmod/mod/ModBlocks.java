package net.spit365.lulasmod.mod;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.spit365.lulasmod.block.PentagrammBlock;
import net.spit365.lulasmod.block.SpellPedestalBlock;
import net.spit365.lulasmod.block.TitrationStandBlock;
import net.spit365.lulasmod.util.RegisterHelper;

public final class ModBlocks {
	public record BlockAndItem<T extends Block>(T block, BlockItem item) {}
	public static final BlockAndItem<SpellPedestalBlock> SPELL_PEDESTAL = RegisterHelper.block(
		"spell_pedestal",
		SpellPedestalBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.strength(-1.0F, Float.MAX_VALUE)
			.noLootTable()
			.isValidSpawn(Blocks::never));
	public static final BlockAndItem<PentagrammBlock> PENTAGRAMM = RegisterHelper.block(
		"pentagramm",
		PentagrammBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.COLOR_RED)
			.noCollission()
			.instabreak()
			.pushReaction(PushReaction.DESTROY)
			.isValidSpawn(Blocks::never));
	public static final BlockAndItem<TitrationStandBlock> TITRATION_STAND = RegisterHelper.block(
		"titration_stand",
		TitrationStandBlock::new,
		BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.strength(0.5F)
			.lightLevel(state -> 1)
			.noOcclusion());

	public static void init() {}
}
