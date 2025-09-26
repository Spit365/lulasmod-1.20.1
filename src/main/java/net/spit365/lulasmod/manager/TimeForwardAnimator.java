package net.spit365.lulasmod.manager;

import net.minecraft.client.world.ClientWorld;

public final class TimeForwardAnimator {
    private final ClientWorld world;
    private long baseAbsTime;
    private long baseDayTime;
    private long forwardCounter = 0;
    private boolean running = false;

    public TimeForwardAnimator(ClientWorld world) {
        this.world = world;
    }

    private static long normDay(long t) {
        long m = t % 24000L;
        return m < 0 ? m + 24000L : m;
    }

    public void start() {
        if (world == null || running) return;
        running = true;

        baseAbsTime = world.getTime();
        baseDayTime = world.getTimeOfDay();

        world.setTime(baseAbsTime, baseDayTime, false);
        forwardCounter = 0;
    }

    public void tick() {
        if (!running || world == null) return;

        long day = baseDayTime;

        if (forwardCounter < 300) {
            forwardCounter += 2;
            day = normDay(day + forwardCounter);
        } else if (forwardCounter < 600) {
            forwardCounter += 1;
            day = normDay(day + 1200);
        } else {
            stopAndResync();
            return;
        }
        long abs = baseAbsTime + forwardCounter;
        world.setTime(abs, day, false);
    }

    public void stopAndResync() {
        if (!running || world == null) return;
        running = false;
        world.setTime(world.getTime(), world.getTimeOfDay(), true);
    }

    public boolean isRunning() { return running; }
}
