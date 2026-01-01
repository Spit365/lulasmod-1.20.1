package net.spit365.lulasmod.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModMethods;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.packet.SetTimeForwardAnimationStateS2CPacket;

public class TimeForward {
    public static final int ANIMATION_DURATION = 450;

    @Environment(EnvType.CLIENT)
    public static final class Animator {
        private static long displayTime;
        private static int animationDuration = 0;
        private static boolean running = false;
        private static final long DAY_LENGTH = 24000L;

        public static void init() {
            WorldRenderEvents.START.register((context) -> {
                if (running) update(context);
            });
            ClientTickEvents.END_CLIENT_TICK.register(client -> Animator.tick(client.world));
        }

        private static long normDay(long t) {
            long m = t % DAY_LENGTH;
            return m < 0 ? m + DAY_LENGTH : m;
        }

        public static void start(ClientWorld world) {
            if (running) return;
            running = true;
            displayTime = world.getTimeOfDay();
            animationDuration = 0;
        }

        public static void tick(ClientWorld world) {
            if (!running || world == null) return;
            MinecraftServer server = world.getServer();
            if (server != null && server.isPaused()) return;
            if (animationDuration <= ANIMATION_DURATION)
                displayTime += animationDuration;
             else stop();
            animationDuration += 1;
        }

        public static void update(WorldRenderContext context){
            ClientWorld world = context.world();
            if (!running || world == null) return;
            world.getLevelProperties().setTimeOfDay(normDay(
                    displayTime + (long) (animationDuration * context.tickCounter().getTickProgress(false))
            ));
        }

        public static void stop() {
            if (!running) return;
            running = false;
        }

        public static boolean isRunning() {
            return running;
        }
    }
    public static class ServerLogic {
        public record VisualContext(int frames, Vec3d pos){}

        public static void tick(ServerPlayerEntity player){
            VisualContext context = player.getAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
            if (context == null) return;
            Vec3d pos = context.pos();
            Box box = new Box(pos.add(-2), pos.add(2));
            if (!box.contains(player.getPos())){
                player.removeAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
                ServerPlayNetworking.send(player, new SetTimeForwardAnimationStateS2CPacket(false));
                return;
            }
            int frames = context.frames();
            if (frames > 0) {
                player.setAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES, new VisualContext(frames -1, pos));
                ModMethods.outlineBox(box, player.getWorld(), ModParticles.CURSED_BLOOD, 0.0625);
            } else {
                player.removeAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
                ModMethods.pocketTeleport(player);
            }
        }
    }
}
