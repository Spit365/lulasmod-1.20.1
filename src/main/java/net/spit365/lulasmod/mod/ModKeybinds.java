package net.spit365.lulasmod.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.spit365.lulasmod.packet.CycleSpellHotbarC2SPacket;
import net.spit365.lulasmod.packet.DashC2SPacket;
import net.spit365.lulasmod.packet.DemonContractC2SPacket;
import net.spit365.lulasmod.util.ClientRegisterHelper;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class ModKeybinds {
	public static final KeyBinding CYCLE_SPELL_KEY = ClientRegisterHelper.keyBinding("cycle_spell", GLFW.GLFW_KEY_R);
	public static final KeyBinding DEMON_CONTRACT_KEY = ClientRegisterHelper.keyBinding("demon_contract", GLFW.GLFW_KEY_BACKSLASH);
    public static final KeyBinding DASH_KEY = ClientRegisterHelper.keyBinding("dash", GLFW.GLFW_KEY_C);

	public static void init(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeybinds.CYCLE_SPELL_KEY.wasPressed()) ClientPlayNetworking.send(new CycleSpellHotbarC2SPacket());
            if (ModKeybinds.DEMON_CONTRACT_KEY.wasPressed()) ClientPlayNetworking.send(new DemonContractC2SPacket());
            if (ModKeybinds.DASH_KEY.isPressed()) ClientPlayNetworking.send(new DashC2SPacket());
        });
    }
}
