package net.spit365.lulasmod;

import net.fabricmc.api.ClientModInitializer;
import net.spit365.lulasmod.mod.ModClientEvents;
import net.spit365.lulasmod.mod.ModKeybinds;

public class LulasmodClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModClientEvents.init();
        ModKeybinds.init();
    }


}