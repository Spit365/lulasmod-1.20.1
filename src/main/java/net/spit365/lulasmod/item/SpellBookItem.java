package net.spit365.lulasmod.item;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.util.ModUtil;
import net.spit365.lulasmod.util.SpellHotbar;

import java.util.List;
import java.util.function.Function;

public class SpellBookItem extends Item implements SpellHotbar {
    public SpellBookItem(Settings settings) {
        super(settings);
    }

    @Override
    public List<Identifier> getHotbarList(PlayerEntity player) {
        ItemStack stack = (player.getMainHandStack().isOf(this) ? player.getMainHandStack() : player.getOffHandStack());
        return stack.get(ModData.SPELL_BOOK_SPELLS);
    }

    @Override
    public void onCycle(PlayerEntity player,  Function<List<Identifier>, List<Identifier>> cycleFunction) {
        ItemStack stack = (player.getMainHandStack().getItem().equals(this) ? player.getMainHandStack() : player.getOffHandStack());
        stack.set(ModData.SPELL_BOOK_SPELLS, cycleFunction.apply(stack.get(ModData.SPELL_BOOK_SPELLS)));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world instanceof ServerWorld) {
            ItemStack spellbook = player.getStackInHand(hand);
            ItemStack spell = (hand.equals(Hand.MAIN_HAND) ? player.getOffHandStack() : player.getMainHandStack());
            List<Identifier> mutable = ModUtil.makeMutable(spellbook.get(ModData.SPELL_BOOK_SPELLS));
            if (spell.getItem() instanceof SpellItem) {
                Identifier id = Registries.ITEM.getId(spell.getItem());
                mutable.add(id);
                spellbook.set(ModData.SPELL_BOOK_SPELLS, mutable);
                spell.decrement(1);
                return TypedActionResult.success(spellbook);
            } else if (!mutable.isEmpty()) {
                Identifier id = mutable.getFirst();
                mutable.remove(id);
                spellbook.set(ModData.SPELL_BOOK_SPELLS, mutable);
                player.giveItemStack(new ItemStack(Registries.ITEM.get(id)));
                return TypedActionResult.success(spellbook);
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
