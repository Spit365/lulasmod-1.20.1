package net.spit365.lulasmod.custom.item;

import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spit365.lulasmod.mod.ModData;

import java.util.List;
import java.util.function.Consumer;

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
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !(player.getOffHandStack().getItem() instanceof SpellBookItem)){
            player.getItemCooldownManager().set(player.getStackInHand(hand), 5);
            world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);

			List<Identifier> list = player.getAttached(ModData.EQUIPPED_SPELLS);
			if (list != null) {
				if (player.isSneaking()) list.remove(getSpellName());
				else if (!list.contains(getSpellName())) list.add(getSpellName());
				player.setAttached(ModData.EQUIPPED_SPELLS, list);
				return ActionResult.SUCCESS;
			}
        }
        return ActionResult.PASS;
    }
    private Identifier getSpellName() {return Registries.ITEM.getId(this);}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("spell." + Registries.ITEM.getId(this).getNamespace() + ".tooltip." + Registries.ITEM.getId(this).getPath()));
    }

}