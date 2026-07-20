package net.spit365.lulasmod.mod;

import net.minecraft.client.gui.screens.MenuScreens;
import net.spit365.lulasmod.screen.TitrationStandScreen;

public final class ClientScreens {
    public static void init() {
        MenuScreens.register(ModScreenHandlers.TITRATION_STAND_SCREEN_HANDLER, TitrationStandScreen::new);
    }
}
