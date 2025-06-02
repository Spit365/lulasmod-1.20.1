package net.spit365.lulasmod.custom.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.Mod;
import net.spit365.lulasmod.manager.TagManager;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public abstract class SpellItem extends Item {
    public final int cooldown;
    protected SoundEvent sound = SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE;

    public SpellItem(Integer cooldown, SoundEvent soundEvent) {
        super(new Item.Settings().maxCount(1));
        this.cooldown = cooldown;
        this.sound = soundEvent;

    }
    public SpellItem(Integer cooldown) {
        super(new Item.Settings().maxCount(1));
        this.cooldown = cooldown;
    }

    public abstract void cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier);


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !(player.getOffHandStack().getItem() instanceof SpellBookItem)){
            player.getItemCooldownManager().set(this, 5);
            world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
            LinkedList<Identifier> list = TagManager.readList(player, Mod.TagCategories.EQUIPPED_SPELLS);
            if (player.isSneaking()) list.remove(getSpellName());
            else if (!list.contains(getSpellName())) list.add(getSpellName());
            TagManager.put(player, Mod.TagCategories.EQUIPPED_SPELLS, list);
            return TypedActionResult.success(player.getStackInHand(hand));
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
    private Identifier getSpellName() {return Registries.ITEM.getId(this);}

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("spell." + Registries.ITEM.getId(this).getNamespace() + ".tooltip." + Registries.ITEM.getId(this).getPath()));
    }
}