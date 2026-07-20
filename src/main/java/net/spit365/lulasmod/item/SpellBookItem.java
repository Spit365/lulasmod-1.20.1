package net.spit365.lulasmod.item;


import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.util.ModUtil;
import net.spit365.lulasmod.util.SpellHotbar;

import java.util.List;
import java.util.function.Function;

public class SpellBookItem extends Item implements SpellHotbar {
    public SpellBookItem(Properties settings) {
        super(settings);
    }

    @Override
    public List<ResourceLocation> getHotbarList(Player player) {
        ItemStack stack = (player.getMainHandItem().is(this) ? player.getMainHandItem() : player.getOffhandItem());
        return stack.get(ModData.SPELL_BOOK_SPELLS);
    }

    @Override
    public void onCycle(Player player,  Function<List<ResourceLocation>, List<ResourceLocation>> cycleFunction) {
        ItemStack stack = (player.getMainHandItem().getItem().equals(this) ? player.getMainHandItem() : player.getOffhandItem());
        stack.set(ModData.SPELL_BOOK_SPELLS, cycleFunction.apply(stack.get(ModData.SPELL_BOOK_SPELLS)));

    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (world instanceof ServerLevel) {
            ItemStack spellbook = player.getItemInHand(hand);
            ItemStack spell = (hand.equals(InteractionHand.MAIN_HAND) ? player.getOffhandItem() : player.getMainHandItem());
            List<ResourceLocation> mutable = ModUtil.makeMutable(spellbook.get(ModData.SPELL_BOOK_SPELLS));
            if (spell.getItem() instanceof SpellItem) {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(spell.getItem());
                mutable.add(id);
                spellbook.set(ModData.SPELL_BOOK_SPELLS, mutable);
                spell.shrink(1);
                return InteractionResult.SUCCESS;
            } else if (!mutable.isEmpty()) {
                ResourceLocation id = mutable.getFirst();
                mutable.remove(id);
                spellbook.set(ModData.SPELL_BOOK_SPELLS, mutable);
                player.addItem(new ItemStack(BuiltInRegistries.ITEM.getValue(id)));
                return InteractionResult.SUCCESS;
            }

        }
        return InteractionResult.PASS;
    }
}
