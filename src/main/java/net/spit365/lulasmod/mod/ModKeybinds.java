package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeybinds {
    private static KeyBinding cycleSpellKey;

    public static void init() {
        cycleSpellKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lulasmod.cycle_spell",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.lulasmod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (cycleSpellKey.wasPressed() && client.player != null) ClientPlayNetworking.send(ModPackets.CYCLE_PLAYER_SPELL, PacketByteBufs.create());
        });
    }
}