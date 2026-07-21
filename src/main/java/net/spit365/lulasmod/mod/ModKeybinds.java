package net.spit365.lulasmod.mod;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.spit365.lulasmod.packet.BreakWithMindC2SPacket;
import net.spit365.lulasmod.packet.CycleSpellHotbarC2SPacket;
import net.spit365.lulasmod.packet.DashC2SPacket;
import net.spit365.lulasmod.packet.DemonContractC2SPacket;
import net.spit365.lulasmod.util.ClientRegisterHelper;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class ModKeybinds {
	public static final KeyMapping CYCLE_SPELL_KEY = ClientRegisterHelper.packetKeyMapping("cycle_spell", GLFW.GLFW_KEY_R, true, CycleSpellHotbarC2SPacket::new);
	public static final KeyMapping DEMON_CONTRACT_KEY = ClientRegisterHelper.packetKeyMapping("demon_contract", GLFW.GLFW_KEY_BACKSLASH, false, DemonContractC2SPacket::new);
    public static final KeyMapping DASH_KEY = ClientRegisterHelper.packetKeyMapping("dash", GLFW.GLFW_KEY_C, true, DashC2SPacket::new);
    public static final KeyMapping BREAK_WITH_MIND = ClientRegisterHelper.packetKeyMapping("break_with_mind", GLFW.GLFW_KEY_G, true, BreakWithMindC2SPacket::new);

	public static void init() {}
}
