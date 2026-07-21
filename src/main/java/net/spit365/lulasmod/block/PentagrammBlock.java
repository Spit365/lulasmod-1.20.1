package net.spit365.lulasmod.block;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PentagrammBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 1, 16);

    public PentagrammBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        int candles = collectCandles(world, pos);
        if (candles > 0) {
            player.sendOverlayMessage(Component.literal(String.valueOf(candles)));
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private static int collectCandles(Level world, BlockPos pos) {
        int absoluteCandles = 0;
        for (BlockPos possibleCandlePosition : new BlockPos[]{pos.offset(1, 0, 0), pos.offset(0, 0 , 1), pos.offset(-1, 0, 0), pos.offset(0, 0, -1)}) {
            BlockState blockState = world.getBlockState(possibleCandlePosition);
            if (!blockState.is(BlockTags.CANDLES)) continue;
            Integer candles = blockState.getValue(BlockStateProperties.CANDLES);
            if (candles == null) continue;
            absoluteCandles  += candles;
        }
        Optional<Integer> maxCandles = BlockStateProperties.CANDLES.getPossibleValues().stream().max(Integer::compareTo);
        if (maxCandles.isEmpty()) return 0;
        return absoluteCandles * 25 / maxCandles.get();
    }

    @Override public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {return SHAPE;}
}
