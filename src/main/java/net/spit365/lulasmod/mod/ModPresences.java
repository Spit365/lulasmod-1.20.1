package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.spit365.lulasmod.custom.Presence;
import net.spit365.lulasmod.util.RegisterHelper;

public final class ModPresences {
    public static final Presence BREAK_WITH_MIND = RegisterHelper.presence(10);

    public static void breakWithMind(ServerPlayer player, ServerLevel world) {
        if (BREAK_WITH_MIND.has(player)) {
            BlockPos.betweenClosedStream(new AABB(player.position().add(5), player.position().add(-5)))
                .filter(pos -> world.getBlockState(pos).is(ConventionalBlockTags.GLASS_BLOCKS))
                .forEach(pos -> world.destroyBlock(pos, false));
        }
    }
}
