package net.spit365.lulasmod.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.mod.ModMethods;
import net.spit365.lulasmod.mod.ModParticles;

public class MinerRepel {
    public static void tick(ServerPlayerEntity player) {
        if (!player.getCommandTags().contains("miner")) return;
        BlockPos playerPos = player.getBlockPos();
        BlockPos closestPortal = null;
        for (BlockPos pos : BlockPos.stream(
            playerPos.add(-5, -5, -5),
            playerPos.add(5, 5, 5)
        ).toList()) {
            BlockState blockState = player.getWorld().getBlockState(pos);
            if (
                (blockState.isOf(Blocks.END_PORTAL) || blockState.isOf(Blocks.NETHER_PORTAL)) &&
                (closestPortal == null || pos.getSquaredDistance(playerPos) < closestPortal.getSquaredDistance(playerPos))
            ) closestPortal = pos;
        }
        if (closestPortal == null) return;
        ModMethods.outlineBox(Box.enclosing(closestPortal.add(-5, -5, -5), closestPortal.add(5, 5, 5)), player.getWorld(), ModParticles.GOLDEN_SHIMMER, 0.625);
        player.setVelocity(player.getPos().subtract(Vec3d.ofCenter(closestPortal)).normalize());
        player.velocityModified = true;
    }
}
