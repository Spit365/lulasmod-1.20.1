package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.item.seal.SealItem;
import net.spit365.lulasmod.tag.TagManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModClientEvents {
    private static final Identifier SPELL_HOTBAR_TEXTURE = new Identifier(Lulasmod.MOD_ID, "textures/gui/spell_hotbar.png");
    public static LinkedList<ItemStack> spellList = new LinkedList<>();

    public static void init(){
        HudRenderCallback.EVENT.register((context, v) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            if (player.getMainHandStack().getItem() instanceof SealItem) {
                int x =  context.getScaledWindowWidth() / 2 - 92 + (player.getInventory().selectedSlot * 20);
                int y = context.getScaledWindowHeight() - 42;
                context.drawTexture(SPELL_HOTBAR_TEXTURE, x, y -44, 0, 0,24, 64, 24, 64);
                for (int i = 0; i < Math.min(spellList.size(), 3); i++) context.drawItem(spellList.get(i), x +4, y + (i * -20));
            } else if (player.getOffHandStack().getItem() instanceof SealItem){
                int x =  context.getScaledWindowWidth() / 2 - 121;
                int y = context.getScaledWindowHeight() - 42;
                context.drawTexture(SPELL_HOTBAR_TEXTURE, x, y -44, 0, 0,24, 64, 24, 64);
                for (int i = 0; i < Math.min(spellList.size(), 3); i++) context.drawItem(spellList.get(i), x +4, y + (i * -20));
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(ModPackets.PLAYER_SPELL_LIST, (client, handler, buf, responseSender) -> {
            Map<Integer, ItemStack> map = buf.readMap(PacketByteBuf::readInt, PacketByteBuf::readItemStack);
            client.execute(() -> {
                spellList.clear();
                for (int i = 0; i < map.size(); i++) spellList.add(map.get(i));
            });
        });
    }
}
