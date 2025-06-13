package net.spit365.lulasmod.mod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Server;
import net.spit365.lulasmod.custom.SpellHotbar;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedList;
import java.util.Map;
import java.util.stream.StreamSupport;

import static net.spit365.lulasmod.custom.entity.renderer.TailFeatureRenderer.TAILED_PLAYER_LIST;

public class ModClientEvents {
    private static int forwardCounter = 2400;
    private static long timeOfDay = 0;
    private static final LinkedList<ItemStack> SPELL_HOTBAR_LIST = new LinkedList<>();
    private static final Identifier SPELL_HOTBAR_TEXTURE = new Identifier(Server.MOD_ID, "textures/gui/spell_hotbar.png");
    private static final KeyBinding CYCLE_SPELL_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lulasmod.cycle_spell",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.lulasmod"
    ));

    public static void init() {
        HudRenderCallback.EVENT.register((context, v) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            if (StreamSupport.stream(player.getItemsEquipped().spliterator(), false).anyMatch(stack -> stack.getItem() instanceof SpellHotbar)){
                RenderSystem.enableBlend();
                int x =  context.getScaledWindowWidth() / 2 - 121;
                int y = context.getScaledWindowHeight() - 42;
                if (player.getMainHandStack().getItem() instanceof SpellHotbar)
                    x += (player.getInventory().selectedSlot * 20) + 29;
                context.drawTexture(SPELL_HOTBAR_TEXTURE, x, y -44, 0, 0,24, 64, 24, 64);
                for (int i = 0; i < Math.min(SPELL_HOTBAR_LIST.size(), 3); i++) context.drawItem(SPELL_HOTBAR_LIST.get(i), x +4, y + (i * -20));
                RenderSystem.disableBlend();
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (CYCLE_SPELL_KEY.wasPressed() && client.player != null) ClientPlayNetworking.send(ModServer.Packets.CYCLE_PLAYER_SPELL, PacketByteBufs.create());
            ClientWorld world = client.world;
            if (world != null) {
                if (forwardCounter < 300) {
                    forwardCounter += 2;
                    timeOfDay += forwardCounter;
                    world.setTimeOfDay(timeOfDay);
                } else if (forwardCounter < 600) {
                    forwardCounter++;
                    timeOfDay += 1200;
                    world.setTimeOfDay(timeOfDay);
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(ModServer.Packets.SPELL_HOTBAR_LIST, (client, handler, buf, responseSender) -> {
            Map<Integer, ItemStack> map = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readItemStack);
            client.execute(() -> {
                SPELL_HOTBAR_LIST.clear();
                for (int i = 0; i < map.size(); i++) SPELL_HOTBAR_LIST.add(map.get(i));
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ModServer.Packets.TAILED_PLAYER_LIST, (client, handler, buf, responseSender) -> {
            Map<Integer, String> map = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readString);
            client.execute(() -> {
                TAILED_PLAYER_LIST.clear();
                for (int i = 0; i < map.size(); i++) TAILED_PLAYER_LIST.add(map.get(i));
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(ModServer.Packets.TIME_FORWARD_ANIMATION, (client, handler, buf, responseSender) ->
                client.execute(() -> {
                    forwardCounter = 1;
                    if (client.world != null) timeOfDay = client.world.getTimeOfDay();
        }));
    }
}