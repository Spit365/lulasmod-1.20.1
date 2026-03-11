package net.spit365.lulasmod.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.spit365.lulasmod.blockentity.TitrationStandBlockEntity;
import org.jetbrains.annotations.Nullable;

public class TitrationStandBlock extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = VoxelShapes.union(Block.createColumnShape(2.0, 2.0, 14.0), Block.createColumnShape(14.0, 0.0, 2.0));
    public static final MapCodec<TitrationStandBlock> CODEC = TitrationStandBlock.createCodec(TitrationStandBlock::new);

    public TitrationStandBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TitrationStandBlockEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, ServerWorld world, BlockPos pos, boolean moved) {
        if(this != state.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TitrationStandBlockEntity titrationStandBlockEntity) {
                ItemScatterer.spawn(world, pos, titrationStandBlockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, moved);
        }
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return super.getRenderType(state);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof TitrationStandBlockEntity titrationStandBlockEntity))
            return ActionResult.PASS;
        player.openHandledScreen(titrationStandBlockEntity);
        return ActionResult.SUCCESS;
    }
}
