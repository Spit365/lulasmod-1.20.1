package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelExtractionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.HitResult;
import net.spit365.lulasmod.custom.TimeForward;

@Environment(EnvType.CLIENT)
public final class TimeForwardRenderer {
    private static long displayTime;
    private static int animationDuration = 0;
    private static boolean running = false;
    private static final long DAY_LENGTH = 24000L;

    public static void init() {
        LevelExtractionEvents.AFTER_BLOCK_OUTLINE_EXTRACTION.register(TimeForwardRenderer::render);
        ClientTickEvents.END_CLIENT_TICK.register(TimeForwardRenderer::tick);
    }

    private static void tick(Minecraft client) {
        if (!running) return;
        ClientLevel world = client.level;
        if (world == null) return;
        MinecraftServer server = world.getServer();
        if (server != null && server.isPaused()) return;
        if (animationDuration <= TimeForward.ANIMATION_DURATION)
            displayTime += animationDuration;
        else stop();
        animationDuration += 1;
    }

    private static void render(LevelExtractionContext context, HitResult hit) {
        if (!running) return;
        long m = (displayTime + (long) (animationDuration * context.deltaTracker().getGameTimeDeltaPartialTick(false))) % DAY_LENGTH;
        context.levelState().gameTime = (m < 0 ? m + DAY_LENGTH : m);
    }

    public static void start(ClientLevel world) {
        if (running) return;
        running = true;
        displayTime = world.getGameTime();
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
