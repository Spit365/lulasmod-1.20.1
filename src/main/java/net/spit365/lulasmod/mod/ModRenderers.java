package net.spit365.lulasmod.mod;

import net.spit365.lulasmod.renderer.BoxOutlineRenderer;
import net.spit365.lulasmod.renderer.LinkedLightningRender;
import net.spit365.lulasmod.renderer.TimeForwardRenderer;

public class ModRenderers {
    public static void init(){
        TimeForwardRenderer.init();
        LinkedLightningRender.init();
        BoxOutlineRenderer.init();

    }
}
