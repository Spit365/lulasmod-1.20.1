package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModTagCategories;
import net.spit365.lulasmod.tag.TagManager;

import java.util.LinkedList;

public class IncantationItem extends Item {
    private final String spellName;
    public IncantationItem(Settings settings, String spellName) {
        super(settings);
        this.spellName = spellName;
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()){
            player.getItemCooldownManager().set(this, 5);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS);
            LinkedList<String> list = new LinkedList<>(TagManager.readList(player, ModTagCategories.SPELLS));
            if (player.isSneaking() && list.contains(getSpellName()))list.remove(getSpellName()); else list.add(getSpellName());
            TagManager.put(player, ModTagCategories.SPELLS, list);
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }

    public String getSpellName() {
        return spellName;
    }
}
