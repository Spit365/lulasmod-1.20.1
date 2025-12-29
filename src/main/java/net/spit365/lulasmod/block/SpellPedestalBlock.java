package net.spit365.lulasmod.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.spit365.lulasmod.item.spell.ConjuringItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModMethods;
import net.spit365.lulasmod.mod.ModSpells;

import java.util.List;

public class SpellPedestalBlock extends Block {
    public SpellPedestalBlock(Settings settings) {super(settings);}

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16.0, 12.0, 16.0);

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
		if (world.isClient()) return ActionResult.PASS;
		List<WorldBlockPos> mutable = ModMethods.makeMutable(player.getAttached(ModData.ABSORBED_PEDESTALS));
		WorldBlockPos worldBlockPos = new WorldBlockPos(world.getRegistryKey(), blockPos);
		if (!mutable.contains(worldBlockPos)) {
			mutable.add(worldBlockPos);
			List<ItemStack> spells = ModSpells.SpellTabItems;
			spells.removeIf(spell -> spell.getItem() instanceof ConjuringItem || spell.isOf(ModSpells.HIGHLIGHTER_SPELL));
			if (mutable.size() <= spells.size()) {
				player.setAttached(ModData.ABSORBED_PEDESTALS,  mutable);
				((ServerWorld) world).spawnParticles(ParticleTypes.CRIMSON_SPORE, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 500, 1.5, 1.5, 1.5, 0);
				player.giveItemStack(spells.get(mutable.size() - 1));
			} else player.sendMessage(Text.translatable("notify.lulasmod.pedestal.all_spells"), true);
			return ActionResult.SUCCESS;
		} else player.sendMessage(Text.translatable("notify.lulasmod.already_absorbed_pedestal"), true);
		return ActionResult.PASS;
    }
    @Override public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){return SHAPE;}

	public record WorldBlockPos(RegistryKey<World> world, BlockPos blockPos){
		public static final Codec<WorldBlockPos> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("world").xmap(id -> RegistryKey.of(RegistryKeys.WORLD, id), RegistryKey::getValue)
				.forGetter(WorldBlockPos::world),
			BlockPos.CODEC.fieldOf("blockPos")
				.forGetter(WorldBlockPos::blockPos)
		).apply(instance, WorldBlockPos::new));
	}
}