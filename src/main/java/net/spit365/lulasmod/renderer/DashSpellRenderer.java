package net.spit365.lulasmod.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.spit365.lulasmod.custom.DashSpell;
import net.spit365.lulasmod.item.SealItem;
import net.spit365.lulasmod.mod.ModSpells;

@Environment(EnvType.CLIENT)
public final class DashSpellRenderer {
    public static void render(DrawContext context){
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        SealItem sealItem = null;
        for (Hand hand : Hand.values()) {
            if (player.getStackInHand(hand).getItem() instanceof SealItem item) {
                sealItem = item;
                break;
            }
        }
        if (sealItem == null || SpellHotbarRenderer.spellHotbarList.isEmpty() || !SpellHotbarRenderer.spellHotbarList.getFirst().isOf(ModSpells.DASH_SPELL)) return;

        int x = context.getScaledWindowWidth() / 2 - 5;
        int y = context.getScaledWindowHeight() / 2 - 3;

        int maxUsages = Math.max(sealItem.cooldownDivisor * 5, 0);
        if (DashSpell.usages <= 0)  DashSpell.usages = maxUsages;
        int color = DashSpell.usages != 1 ? Colors.WHITE : (player.isOnGround() ? Colors.YELLOW : Colors.RED);
        context.drawText(client.textRenderer, String.valueOf(DashSpell.usages), x - 11, y, color, true);
        context.drawText(client.textRenderer, String.valueOf(maxUsages), x + 15, y, color, true);
    }
}
