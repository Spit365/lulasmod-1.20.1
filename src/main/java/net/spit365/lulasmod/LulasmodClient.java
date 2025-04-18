package net.spit365.lulasmod;

import net.fabricmc.api.ClientModInitializer;
import net.spit365.lulasmod.mod.ModClientEvents;
import net.spit365.lulasmod.mod.ModKeybinds;
import net.spit365.lulasmod.mod.ModPackets;

public class LulasmodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModPackets.init();
        ModClientEvents.init();
        ModKeybinds.init();
    }


}