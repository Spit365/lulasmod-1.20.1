package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.manager.SpellManager;
import net.spit365.lulasmod.custom.item.seal.SealItem;

import java.util.List;

public class ModClientEvents {
    private static final Identifier SPELL_HOTBAR_TEXTURE = new Identifier(Lulasmod.MOD_ID, "textures/gui/spell_hotbar.png");

    public static void init(){
        HudRenderCallback.EVENT.register((context, v) -> {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;
            if (player.getMainHandStack().getItem() instanceof SealItem) {
                List<ItemStack> spells = SpellManager.getSpells(player);
                if (!spells.isEmpty()) {
                    int hotbarSlotX =  context.getScaledWindowWidth() / 2 - 92 + (player.getInventory().selectedSlot * 20);
                    int hotbarSlotY = context.getScaledWindowHeight() - 42;

                    context.drawTexture(SPELL_HOTBAR_TEXTURE, hotbarSlotX, hotbarSlotY -44, 0, 0,24, 64, 24, 64);
                    for (int i = 0; i < Math.min(spells.size(), 3); i++) {
                        context.drawItem(spells.get(i), hotbarSlotX +4, hotbarSlotY + (i * -20));
                    }
                }
            }
        });

    }
}
