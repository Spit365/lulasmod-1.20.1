package net.spit365.lulasmod.custom.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModItems;
import net.spit365.lulasmod.mod.ModSpells;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SpellPedestalBlock extends Block {
    public SpellPedestalBlock(Settings settings) {super(settings);}

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16.0, 12.0, 16.0);

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos blockPos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient()){
            List<ModData.WorldBlockPos> list = player.getAttached(ModData.ABSORBED_PEDESTALS);
			List<ModData.WorldBlockPos> mutable;
			if (list != null) mutable = new LinkedList<>(list);
			else mutable = new LinkedList<>();
			ModData.WorldBlockPos worldBlockPos = new ModData.WorldBlockPos(world.getRegistryKey(), blockPos);
			if (!mutable.contains(worldBlockPos)) {
				mutable.add(worldBlockPos);
				List<Identifier> spells = ModSpells.SpellTabItems;
				Set<Item> excluded = new HashSet<>(ModItems.tailedExclusive);
				excluded.add(ModSpells.HIGHLIGHTER_SPELL);
				spells.removeIf(id -> excluded.contains(Registries.ITEM.get(id)));
				if (mutable.size() <= spells.size()) {
					player.setAttached(ModData.ABSORBED_PEDESTALS,  mutable);
					((ServerWorld) world).spawnParticles(ParticleTypes.CRIMSON_SPORE, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 500, 1.5, 1.5, 1.5, 0);
					player.giveItemStack(new ItemStack(Registries.ITEM.get(spells.get(mutable.size() - 1))));
				} else player.sendMessage(Text.translatable("notify.lulasmod.pedestal.all_spells"), true);
				return ActionResult.SUCCESS;
			} else player.sendMessage(Text.translatable("notify.lulasmod.already_absorbed_pedestal"), true);
        }
        return ActionResult.PASS;
    }
    @Override public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){return SHAPE;}
}