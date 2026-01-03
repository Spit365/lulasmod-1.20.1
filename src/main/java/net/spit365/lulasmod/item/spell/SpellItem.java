package net.spit365.lulasmod.item.spell;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spit365.lulasmod.item.SpellBookItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModMethods;

import java.util.List;

public class SpellItem extends Item {
	public final Spell spell;

	public SpellItem(Settings settings, Spell spell) {
        super(settings);
		this.spell = spell;
    }

	@FunctionalInterface public interface Spell {int cast(ServerWorld world, PlayerEntity player, Hand hand, float potencyMultiplier, int cooldownMultiplier);}

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !(player.getOffHandStack().getItem() instanceof SpellBookItem)){
            player.getItemCooldownManager().set(player.getStackInHand(hand), 5);
			List<Identifier> mutable = ModMethods.makeMutable(player.getAttached(ModData.EQUIPPED_SPELLS));
            SoundEvent sound = null;
            if (player.isSneaking()) {
                if (mutable.remove(getSpellName()))
                    sound = getSound(false);
            }
			else if (!mutable.contains(getSpellName())) {
                mutable.add(getSpellName());
                sound = getSound(true);
            }
            if (sound != null) world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
			player.setAttached(ModData.EQUIPPED_SPELLS, mutable);
			return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }
    private Identifier getSpellName() {return Registries.ITEM.getId(this);}
	protected SoundEvent getSound(boolean add){return add ? SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE : SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value();}
}