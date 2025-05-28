package net.spit365.lulasmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.custom.entity.renderer.TailFeatureRenderer;
import net.spit365.lulasmod.mod.Mod;
import org.lwjgl.glfw.GLFW;
import java.util.LinkedList;
import java.util.Map;

public class LulasmodClient implements ClientModInitializer {
    private static final Identifier SPELL_HOTBAR_TEXTURE = new Identifier(Lulasmod.MOD_ID, "textures/gui/spell_hotbar.png");
    private static final LinkedList<ItemStack> spellList = new LinkedList<>();
    private static final KeyBinding cycleSpellKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.lulasmod.cycle_spell",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "category.lulasmod"
    ));

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register((context, v) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            if (player.getMainHandStack().getItem() instanceof SpellHotbar) {
                int x =  context.getScaledWindowWidth() / 2 - 92 + (player.getInventory().selectedSlot * 20);
                int y = context.getScaledWindowHeight() - 42;
                context.drawTexture(SPELL_HOTBAR_TEXTURE, x, y -44, 0, 0,24, 64, 24, 64);
                for (int i = 0; i < Math.min(spellList.size(), 3); i++) context.drawItem(spellList.get(i), x +4, y + (i * -20));
            } else if (player.getOffHandStack().getItem() instanceof SpellHotbar){
                int x =  context.getScaledWindowWidth() / 2 - 121;
                int y = context.getScaledWindowHeight() - 42;
                context.drawTexture(SPELL_HOTBAR_TEXTURE, x, y -44, 0, 0,24, 64, 24, 64);
                for (int i = 0; i < Math.min(spellList.size(), 3); i++) context.drawItem(spellList.get(i), x +4, y + (i * -20));
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (cycleSpellKey.wasPressed() && client.player != null) ClientPlayNetworking.send(Mod.Packets.CYCLE_PLAYER_SPELL, PacketByteBufs.create());
        });
        ClientPlayNetworking.registerGlobalReceiver(Mod.Packets.PLAYER_SPELL_LIST, (client, handler, buf, responseSender) -> {
            Map<Integer, ItemStack> map = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readItemStack);
            client.execute(() -> {
                spellList.clear();
                for (int i = 0; i < map.size(); i++) spellList.add(map.get(i));
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Mod.Packets.TAILED_PLAYER_LIST, (client, handler, buf, responseSender) -> {
            Map<Integer, String> map = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readString);
            client.execute(() -> {
                TailFeatureRenderer.tailedPlayerList.clear();
                for (int i = 0; i < map.size(); i++) TailFeatureRenderer.tailedPlayerList.add(map.get(i));
            });
        });
    }


}