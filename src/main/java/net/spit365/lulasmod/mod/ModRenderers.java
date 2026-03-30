package net.spit365.lulasmod.mod;

import net.spit365.lulasmod.renderer.LinkedLightningRender;
import net.spit365.lulasmod.renderer.TimeForwardRenderer;

public final class ModRenderers {
    public static void init() {
        TimeForwardRenderer.init();
        LinkedLightningRender.init();
    }
}
