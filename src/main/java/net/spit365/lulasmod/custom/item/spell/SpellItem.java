package net.spit365.lulasmod.custom.item.spell;

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
import net.spit365.lulasmod.custom.item.SpellBookItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModMethods;

import java.util.List;
import java.util.function.Consumer;

public class SpellItem extends Item {
	public final Spell spell;

	public SpellItem(Settings settings, Spell spell) {
        super(settings);
		this.spell = spell;
    }

	@FunctionalInterface public interface Spell {int cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier);}

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !(player.getOffHandStack().getItem() instanceof SpellBookItem)){
            player.getItemCooldownManager().set(player.getStackInHand(hand), 5);
            world.playSound(null, player.getBlockPos(), getSound(), SoundCategory.PLAYERS);
			List<Identifier> mutable = ModMethods.makeMutable(player.getAttached(ModData.EQUIPPED_SPELLS));
			if (player.isSneaking()) mutable.remove(getSpellName());
			else if (!mutable.contains(getSpellName())) mutable.add(getSpellName());
			player.setAttached(ModData.EQUIPPED_SPELLS, mutable);
			return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    private Identifier getSpellName() {return Registries.ITEM.getId(this);}
	protected SoundEvent getSound(){return SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE;}

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("spell." + Registries.ITEM.getId(this).getNamespace() + ".tooltip." + Registries.ITEM.getId(this).getPath()));
    }

}