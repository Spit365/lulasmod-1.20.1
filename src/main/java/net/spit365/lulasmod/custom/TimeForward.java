package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.util.ModUtil;
import net.spit365.lulasmod.packet.SetTimeForwardAnimationStateS2CPacket;

public class TimeForward {
    public static final int ANIMATION_DURATION = 450;

    public static void tick(ServerPlayerEntity player){
        VisualContext context = player.getAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
        if (context == null) return;
        Vec3d pos = context.pos();
        Box box = new Box(pos.add(-5), pos.add(5));
        if (!box.contains(player.getPos())){
            player.removeAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
            ServerPlayNetworking.send(player, new SetTimeForwardAnimationStateS2CPacket(false));
            return;
        }
        int frames = context.frames();
        if (frames > 0) {
            player.setAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES, new VisualContext(frames -1, pos));
            BoxOutlineState.add(box, 0xFFAA0000);
        } else {
            player.removeAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
            ModUtil.pocketTeleport(player);
        }
    }

    public record VisualContext(int frames, Vec3d pos){}
}
