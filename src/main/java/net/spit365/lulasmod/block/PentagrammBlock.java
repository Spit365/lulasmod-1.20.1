package net.spit365.lulasmod.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Optional;

public class PentagrammBlock extends Block {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);

    public PentagrammBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        int candles = collectCandles(world, pos);
        if (candles > 0) {
            player.sendMessage(Text.literal(String.valueOf(candles)), true);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private static int collectCandles(World world, BlockPos pos) {
        int absoluteCandles = 0;
        for (BlockPos possibleCandlePosition : new BlockPos[]{pos.add(1, 0, 0), pos.add(0, 0 , 1), pos.add(-1, 0, 0), pos.add(0, 0, -1)}) {
            BlockState blockState = world.getBlockState(possibleCandlePosition);
            if (!blockState.isIn(BlockTags.CANDLES)) continue;
            Integer candles = blockState.get(Properties.CANDLES);
            if (candles == null) continue;
            absoluteCandles  += candles;
        }
        Optional<Integer> maxCandles = Properties.CANDLES.getValues().stream().max(Integer::compareTo);
        if (maxCandles.isEmpty()) return 0;
        return absoluteCandles * 25 / maxCandles.get();
    }

    @Override public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){return SHAPE;}
}
