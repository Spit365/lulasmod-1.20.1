package net.spit365.lulasmod.item.spell;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.spit365.lulasmod.item.SpellBookItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.util.ModUtil;
import net.spit365.lulasmod.util.Spell;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SpellItem extends Item {
	public final Spell spell;

	public SpellItem(Settings settings, Spell spell) {
        super(settings);
		this.spell = spell;
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient() && !(player.getOffHandStack().getItem() instanceof SpellBookItem)){
            player.getItemCooldownManager().set(player.getStackInHand(hand), 5);
			List<Identifier> mutable = ModUtil.makeMutable(player.getAttached(ModData.EQUIPPED_SPELLS));
            SoundEvent sound = null;
            Identifier spellName = Registries.ITEM.getId(this);
            if (player.isSneaking()) {
                if (mutable.remove(spellName))
                    sound = getSound(false);
            } else if (!mutable.contains(spellName)) {
                mutable.add(spellName);
                sound = getSound(true);
            }
            if (sound != null) world.playSound(null, player.getBlockPos(), sound, SoundCategory.PLAYERS);
			player.setAttached(ModData.EQUIPPED_SPELLS, mutable);
			return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    protected SoundEvent getSound(boolean add){
        return add ? SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE : SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value();
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof PlayerEntity player && !Objects.equals(ModUtil.getInventoryStack(player, stack.getItem()), stack)) {
            player.sendMessage(Text.translatable("notify.lulasmod.duplicate_spell"), true);
            entity.dropStack(world, stack.copy());
            stack.decrement(stack.getCount());
        }
    }
}