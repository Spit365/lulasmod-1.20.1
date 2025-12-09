package net.spit365.lulasmod.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
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
        outlineBox(Box.enclosing(closestPortal.add(-5, -5, -5), closestPortal.add(5, 5, 5)), player.getWorld(), ModParticles.GOLDEN_SHIMMER);
        player.setVelocity(player.getPos().subtract(Vec3d.ofCenter(closestPortal)).normalize());
        player.velocityModified = true;
    }

	private static void outlineBox(Box box, ServerWorld world, SimpleParticleType particle){
		final Vec3d start = box.getCenter().add(box.getLengthX() / -2, box.getLengthY() / -2, box.getLengthZ() / -2);
		final Vec3d end = box.getCenter().add(box.getLengthX() / 2, box.getLengthY() / 2, box.getLengthZ() / 2);

		for (double i = 0; i < box.getLengthX(); i += 0.625) world.spawnParticles(particle, start.getX() + i, start.getY(), start.getZ(), 0, 0, 0, 0, 0);
		for (double i = 0; i < box.getLengthX(); i += 0.625) world.spawnParticles(particle, start.getX() + i, start.getY(), end.getZ(), 0, 0, 0, 0, 0);
		for (double i = 0; i < box.getLengthX(); i += 0.625) world.spawnParticles(particle, start.getX() + i, end.getY(), start.getZ(), 0, 0, 0, 0, 0);
		for (double i = 0; i < box.getLengthX(); i += 0.625) world.spawnParticles(particle, start.getX() + i, end.getY(), end.getZ(), 0, 0, 0, 0, 0);

		for (double i = 0; i < box.getLengthY(); i += 0.625) world.spawnParticles(particle, start.getX(), start.getY() + i, start.getZ(), 0, 0, 0, 0, 0);
		for (double i = 0; i < box.getLengthY(); i += 0.625) world.spawnParticles(particle, start.getX(), start.getY() + i, end.getZ(), 0, 0, 0, 0, 0);
		for (double i = 0; i < box.getLengthY(); i += 0.625) world.spawnParticles(particle, end.getX(), start.getY() + i, start.getZ(), 0, 0, 0, 0, 0);
		for (double i = 0; i < box.getLengthY(); i += 0.625) world.spawnParticles(particle, end.getX(), start.getY() + i, end.getZ(), 0, 0, 0, 0, 0);

		for (double i = 0; i < box.getLengthZ(); i += 0.625) world.spawnParticles(particle, start.getX(), start.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
		for (double i = 0; i < box.getLengthZ(); i += 0.625) world.spawnParticles(particle, start.getX(), end.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
		for (double i = 0; i < box.getLengthZ(); i += 0.625) world.spawnParticles(particle, end.getX(), start.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
		for (double i = 0; i < box.getLengthZ(); i += 0.625) world.spawnParticles(particle, end.getX(), end.getY(), start.getZ() + i, 0, 0, 0, 0, 0);
	}
}
