package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.ModTagCategories;
import net.spit365.lulasmod.tag.TagManager;

import java.util.LinkedList;

public class IncantationItem extends Item {
    public IncantationItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()){
            player.getItemCooldownManager().set(this, 5);
            world.playSound(null, player.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS);
            LinkedList<String> list = new LinkedList<>(TagManager.readList(Lulasmod.MOD_ID, player, ModTagCategories.SPELLS));
            if (player.isSneaking() && list.contains(getSpellName()))
                 list.remove(getSpellName());
            else list.add(getSpellName());
            TagManager.put(Lulasmod.MOD_ID, player, ModTagCategories.SPELLS, list);
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
    private String getSpellName() {
        return Registries.ITEM.getId(this).getPath();
    }
}
