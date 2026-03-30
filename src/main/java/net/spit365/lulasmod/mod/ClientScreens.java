package net.spit365.lulasmod.mod;

import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.spit365.lulasmod.screen.TitrationStandScreen;

public final class ClientScreens {
    public static void init(){
        HandledScreens.register(ModScreenHandlers.TITRATION_STAND_SCREEN_HANDLER, TitrationStandScreen::new);
    }
}
