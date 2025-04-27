package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.event.server.ServerTickCallback;
import net.spit365.lulasmod.custom.item.GoldenTridentItem;
import net.spit365.lulasmod.custom.item.seal.CatalystItem;
import net.spit365.lulasmod.tag.TagManager;

public class ModServerEvents {
    public static void init(){
        ServerTickCallback.EVENT.register(minecraftServer -> {
            for (ServerPlayerEntity player : minecraftServer.getPlayerManager().getPlayerList()) {
                if (player.getCommandTags().contains("miner")) {
                    BlockPos playerPos = player.getBlockPos();
                    int portalRadius = 5;
                    BlockPos closestPortal = null;
                    double closestDistance = Double.MAX_VALUE;
                    for (BlockPos pos : BlockPos.stream(playerPos.add(-portalRadius, -portalRadius, -portalRadius),
                            playerPos.add(portalRadius, portalRadius, portalRadius))
                            .map(BlockPos::toImmutable)
                            .toList()) {
                        if (player.getWorld().getBlockState(pos).isOf(Blocks.END_PORTAL)||
                            player.getWorld().getBlockState(pos).isOf(Blocks.NETHER_PORTAL)) {
                            double distance = pos.getSquaredDistance(playerPos);
                            if (distance < closestDistance) {
                                closestDistance = distance;
                                closestPortal = pos;
                            }
                        }
                    }

                    if (closestPortal != null) {
                        Vec3d repelVec = player.getPos().subtract(Vec3d.ofCenter(closestPortal)).normalize();

                        if (!repelVec.equals(Vec3d.ZERO)) {
                            player.setVelocity(repelVec);
                            player.velocityModified = true;
                        }
                    }
                }
            }
            GoldenTridentItem.impale(minecraftServer);
            ModImportant.updateClientSpellList(minecraftServer);
        });
        ServerPlayNetworking.registerGlobalReceiver(ModPackets.CYCLE_PLAYER_SPELL, (a, player, b, c, d) -> {
            if (
                player.getMainHandStack().getItem() instanceof CatalystItem ||
                player.getOffHandStack().getItem() instanceof CatalystItem
            ) TagManager.cycle(player, ModTagCategories.SPELLS);
        });
    }
}
