package net.spit365.lulasmod.mod;

import net.spit365.lulasmod.custom.NetherDeathSystem;
import net.spit365.lulasmod.custom.Presence;

public final class ModCustom {
    public static void init(){
        NetherDeathSystem.init();
        Presence.init();
    }
}
