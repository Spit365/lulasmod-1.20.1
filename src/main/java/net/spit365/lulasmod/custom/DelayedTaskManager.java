package net.spit365.lulasmod.custom;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DelayedTaskManager {
    private static final List<DelayedTask> tasks = new ArrayList<>();

    static {
        ServerTickEvents.END_SERVER_TICK.register(DelayedTaskManager::onTick);
    }

    public static void addTask(int delayInTicks, Runnable action) {
        tasks.add(new DelayedTask(delayInTicks, action));
    }

    private static void onTick(MinecraftServer server) {
        Iterator<DelayedTask> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            DelayedTask task = iterator.next();
            task.ticksRemaining--;
            if (task.ticksRemaining <= 0) {
                task.action.run();
                iterator.remove(); // Removes completed task
            }
        }
    }

    private static class DelayedTask {
        int ticksRemaining;
        Runnable action;

        DelayedTask(int ticksRemaining, Runnable action) {
            this.ticksRemaining = ticksRemaining;
            this.action = action;
        }
    }
}
