package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModTagCategories;
import net.spit365.lulasmod.tag.TagManager;
import java.util.LinkedList;

public class SpellItem extends Item {
    public SpellItem(Settings settings) {
        super(settings);
    }

    protected int cooldown() {return 5;}
    protected SoundEvent sound() {return SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE;}

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()){
            player.getItemCooldownManager().set(this, cooldown());
            world.playSound(null, player.getBlockPos(), sound(), SoundCategory.PLAYERS);
            LinkedList<Identifier> list = TagManager.readList(player, ModTagCategories.SPELLS);
            if (player.isSneaking()) list.remove(getSpellName());
            else if (!list.contains(getSpellName())) list.add(getSpellName());
            TagManager.put(player, ModTagCategories.SPELLS, list);
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
    private Identifier getSpellName() {return Registries.ITEM.getId(this);}
}