package net.spit365.lulasmod.mod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.spit365.lulasmod.screen.TitrationStandScreenHandler;
import net.spit365.lulasmod.util.RegisterHelper;

public final class ModScreenHandlers {
    public static final MenuType<TitrationStandScreenHandler> TITRATION_STAND_SCREEN_HANDLER = RegisterHelper.screenHandler("titration_stand_screen_handler", TitrationStandScreenHandler::new, BlockPos.STREAM_CODEC);

    public static void init() {}
}
