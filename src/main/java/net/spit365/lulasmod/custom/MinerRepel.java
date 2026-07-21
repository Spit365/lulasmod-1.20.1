package net.spit365.lulasmod.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.spit365.boa.BoxOutline;

import java.util.Set;
import java.util.stream.Collectors;

public final class MinerRepel {
    public static void tick(ServerPlayer player) {
        if (!player.entityTags().contains("miner")) return;
        BlockPos playerPos = player.blockPosition();
        ServerLevel world = player.level();
        Set<AABB> portalAndAdjacent =
            BlockPos.betweenClosedStream(
                playerPos.offset(-5, -5, -5),
                playerPos.offset(5, 5, 5)
            )
            .map(BlockPos::immutable)
            .filter(blockPos -> world.getBlockState(blockPos).is(BlockTags.PORTALS))
            .map(blockPos -> {
                int x = blockPos.getX();
                int y = blockPos.getY();
                int z = blockPos.getZ();
                return new AABB(
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
        BoxOutline.addAll(portalAndAdjacent, 0xFFFFFF00);
        for (AABB box : portalAndAdjacent) {
            for (BlockPos pos : BlockPos.betweenClosedStream(box).map(BlockPos::immutable).collect(Collectors.toSet())) {
                double squaredDistance = pos.distSqr(playerPos);
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
        player.setDeltaMovement(player.position().subtract(Vec3.atCenterOf(closestPortal)).normalize());
        player.hurtMarked = true;
    }
}
