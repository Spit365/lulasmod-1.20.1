package net.spit365.lulasmod.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.spit365.lulasmod.packet.BreakWithMindC2SPacket;
import net.spit365.lulasmod.packet.CycleSpellHotbarC2SPacket;
import net.spit365.lulasmod.packet.DashC2SPacket;
import net.spit365.lulasmod.packet.DemonContractC2SPacket;
import net.spit365.lulasmod.util.ClientRegisterHelper;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class ModKeybinds {
	public static final KeyBinding CYCLE_SPELL_KEY = ClientRegisterHelper.packetKeyBinding("cycle_spell", GLFW.GLFW_KEY_R, true, CycleSpellHotbarC2SPacket::new);
	public static final KeyBinding DEMON_CONTRACT_KEY = ClientRegisterHelper.packetKeyBinding("demon_contract", GLFW.GLFW_KEY_BACKSLASH, false, DemonContractC2SPacket::new);
    public static final KeyBinding DASH_KEY = ClientRegisterHelper.packetKeyBinding("dash", GLFW.GLFW_KEY_C, true, DashC2SPacket::new);
    public static final KeyBinding BREAK_WITH_MIND = ClientRegisterHelper.packetKeyBinding("break_with_mind", GLFW.GLFW_KEY_G, true, BreakWithMindC2SPacket::new);

	public static void init() {}
}
