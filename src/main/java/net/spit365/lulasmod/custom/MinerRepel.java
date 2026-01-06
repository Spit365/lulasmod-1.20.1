package net.spit365.lulasmod.custom;

import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Set;
import java.util.stream.Collectors;

public class MinerRepel {
    public static void tick(ServerPlayerEntity player) {
        if (!player.getCommandTags().contains("miner")) return;
        BlockPos playerPos = player.getBlockPos();
        ServerWorld world = player.getWorld();
        Set<Box> portalAndAdjacent =
            BlockPos.stream(
                playerPos.add(-5, -5, -5),
                playerPos.add(5, 5, 5)
            )
            .map(BlockPos::toImmutable)
            .filter(blockPos -> world.getBlockState(blockPos).isIn(BlockTags.PORTALS))
            .map(blockPos -> {
                int x = blockPos.getX();
                int y = blockPos.getY();
                int z = blockPos.getZ();
                return new Box(
                    x - 5d,
                    y - 5d,
                    z - 5d,
                    x + 5d,
                    y + 5d,
                    z + 5d
            );})
            .collect(Collectors.toSet());
        BlockPos closestPortal = null;
        double closestDistance = Double.POSITIVE_INFINITY;
        BoxOutlineState.addAll(portalAndAdjacent, 0xFFFFFF00);
        for (Box box : portalAndAdjacent){
            for (BlockPos pos : BlockPos.stream(box).map(BlockPos::toImmutable).collect(Collectors.toSet())) {
                double squaredDistance = pos.getSquaredDistance(playerPos);
                if (
                    pos != closestPortal ||
                    squaredDistance < closestDistance
                ) {
                    closestPortal = pos;
                    closestDistance = squaredDistance;
                }
            }
        }
        if (closestPortal == null || closestDistance < 1e-10) return;
        player.setVelocity(player.getPos().subtract(Vec3d.ofCenter(closestPortal)).normalize());
        player.velocityModified = true;
    }
}
