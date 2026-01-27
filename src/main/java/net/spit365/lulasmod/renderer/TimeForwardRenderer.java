package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.TimeForward;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public final class TimeForwardRenderer {
    private static long displayTime;
    private static int animationDuration = 0;
    private static boolean running = false;
    private static final long DAY_LENGTH = 24000L;

    public static void init() {
        WorldRenderEvents.START.register(TimeForwardRenderer::render);
        ClientTickEvents.END_CLIENT_TICK.register(TimeForwardRenderer::tick);
    }

    private static void tick(MinecraftClient client) {
        if (!running) return;
        ClientWorld world = client.world;
        if (world == null) return;
        MinecraftServer server = world.getServer();
        if (server != null && server.isPaused()) return;
        if (animationDuration <= TimeForward.ANIMATION_DURATION)
            displayTime += animationDuration;
        else stop();
        animationDuration += 1;
    }

    private static void render(WorldRenderContext context) {
        if (!running) return;
        ClientWorld world = context.world();
        if (world == null) return;
        long m = (displayTime + (long) (animationDuration * context.tickCounter().getTickProgress(false))) % DAY_LENGTH;
        world.getLevelProperties().setTimeOfDay(m < 0 ? m + DAY_LENGTH : m);
    }

    public static void start(ClientWorld world) {
        if (running) return;
        running = true;
        displayTime = world.getTimeOfDay();
        animationDuration = 0;
    }

    public static void stop() {
        if (!running) return;
        running = false;
    }

    public static boolean isRunning() {
        return running;
    }
}
