package net.spit365.lulasmod.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModSpells;
import net.spit365.lulasmod.util.ModUtil;

import java.util.List;

public class SpellPedestalBlock extends Block {
    public SpellPedestalBlock(Properties settings) {super(settings);}

    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16.0, 12.0, 16.0);

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos blockPos, Player player, BlockHitResult hit) {
		if (world.isClientSide()) return InteractionResult.PASS;
		List<WorldBlockPos> mutable = ModUtil.makeMutable(player.getAttached(ModData.ABSORBED_PEDESTALS));
		WorldBlockPos worldBlockPos = new WorldBlockPos(world.dimension(), blockPos);
		if (!mutable.contains(worldBlockPos)) {
			mutable.add(worldBlockPos);
			List<ItemStack> spells = ModSpells.SpellTabItems;
			spells.removeIf(spell -> spell.getItem() instanceof ConjuringItem || spell.is(ModSpells.HIGHLIGHTER_SPELL));
			if (mutable.size() <= spells.size()) {
				player.setAttached(ModData.ABSORBED_PEDESTALS,  mutable);
				((ServerLevel) world).sendParticles(ParticleTypes.CRIMSON_SPORE, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 500, 1.5, 1.5, 1.5, 0);
				player.addItem(spells.get(mutable.size() - 1));
			} else player.displayClientMessage(Component.translatable("notify.lulasmod.pedestal.all_spells"), true);
			return InteractionResult.SUCCESS;
		} else player.displayClientMessage(Component.translatable("notify.lulasmod.already_absorbed_pedestal"), true);
		return InteractionResult.PASS;
    }
    @Override public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {return SHAPE;}

	public record WorldBlockPos(ResourceKey<Level> world, BlockPos blockPos) {
		public static final Codec<WorldBlockPos> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("world").xmap(id -> ResourceKey.create(Registries.DIMENSION, id), ResourceKey::location)
				.forGetter(WorldBlockPos::world),
			BlockPos.CODEC.fieldOf("blockPos")
				.forGetter(WorldBlockPos::blockPos)
		).apply(instance, WorldBlockPos::new));
	}
}