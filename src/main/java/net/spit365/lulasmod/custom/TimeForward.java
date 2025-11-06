package net.spit365.lulasmod.custom;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModMethods;

public class TimeForward {
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
            if (animationDuration <= 450)
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
        public static void tick(ServerPlayerEntity player){
            Integer i = player.getAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
            if (i != null) {
                if (i > 0) player.setAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES, i -1);
                else {
                    player.removeAttached(ModData.TIME_FORWARD_ANIMATION_FRAMES);
                    ModMethods.pocketTeleport(player);
                }
            }
        }
    }
}
