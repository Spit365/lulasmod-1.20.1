package net.spit365.lulasmod.manager;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.spit365.lulasmod.Lulasmod;

public final class TimeForwardAnimator {
    private static long displayTime;
    private static int animationDuration = 0;
    private static boolean running = false;
    private static final long DAY_LENGTH = 24000L;

    public static void init() {
        WorldRenderEvents.START.register((context) -> {
            if (running) update(context);
        });
        Lulasmod.LOGGER.info("init TFA");
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
        Lulasmod.LOGGER.info("TFA start");
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
        Lulasmod.LOGGER.info("TFA stop");
    }

    public static boolean isRunning() {
        return running;
    }
}
