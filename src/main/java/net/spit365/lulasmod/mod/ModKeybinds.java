package net.spit365.lulasmod.mod;

import net.minecraft.client.option.KeyBinding;
import net.spit365.lulasmod.manager.RegisterHelper;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
	static final KeyBinding CYCLE_SPELL_KEY = RegisterHelper.keyBinding("cycle_spell", GLFW.GLFW_KEY_R);

	public static void init(){}
}
