package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.clienttweaks.packet.BoxStateS2CPacket;
import net.spit365.clienttweaks.util.BoxContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class BoxOutline {
    private static final HashSet<BoxContext> state = new HashSet<>();

    public static boolean add(Box box, int color) {
        return state.add(new BoxContext(box, color));
    }
    public static boolean addAll(Collection<Box> box, int color) {
        return state.addAll(box.stream().map(box1 ->  new BoxContext(box1, color)).collect(Collectors.toSet()));
    }

    public static void tick(ServerPlayerEntity player) {
        Set<BoxContext> near = state.stream()
            .filter(ctx -> isNear(player.getPos(), ctx.box()))
            .collect(Collectors.toUnmodifiableSet());
        ServerPlayNetworking.send(player, new BoxStateS2CPacket(near));
        state.clear();
    }

    private static boolean isNear(Vec3d playerPos, Box box) {
        return playerPos.squaredDistanceTo(box.getMinPos().add(box.getMaxPos()).multiply(0.5)) <= 1_000_000;
    }
}
