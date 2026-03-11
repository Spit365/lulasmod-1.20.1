package net.spit365.lulasmod.mod;

import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import net.spit365.lulasmod.screen.TitrationStandScreenHandler;
import net.spit365.lulasmod.util.RegisterHelper;

public final class ModScreenHandlers {
    public static final ScreenHandlerType<TitrationStandScreenHandler> TITRATION_STAND_SCREEN_HANDLER = RegisterHelper.screenHandler("titration_stand_screen_handler", TitrationStandScreenHandler::new, BlockPos.PACKET_CODEC);

    public static void init(){}
}
