package net.spit365.lulasmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.spit365.lulasmod.mod.ModClient;
import net.spit365.lulasmod.mod.ModClientEvents;

@Environment(EnvType.CLIENT)
public class Client implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModClientEvents.init();
        ModClient.init();
    }
}