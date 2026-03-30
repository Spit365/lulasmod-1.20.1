package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.spit365.lulasmod.custom.Presence;
import net.spit365.lulasmod.util.RegisterHelper;

public final class ModPresences {
    public static final Presence BREAK_WITH_MIND = RegisterHelper.presence(10);

    public static void breakWithMind(ServerPlayerEntity player, ServerWorld world) {
        if (BREAK_WITH_MIND.has(player)) {
            BlockPos.stream(new Box(player.getPos().add(5), player.getPos().add(-5)))
                .filter(pos -> world.getBlockState(pos).isIn(ConventionalBlockTags.GLASS_BLOCKS))
                .forEach(pos -> world.breakBlock(pos, false));
        }
    }
}
