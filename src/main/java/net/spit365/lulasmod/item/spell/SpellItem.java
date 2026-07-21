package net.spit365.lulasmod.item.spell;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.spit365.lulasmod.item.SpellBookItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.util.ModUtil;
import net.spit365.lulasmod.util.Spell;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SpellItem extends Item {
	public final Spell spell;

	public SpellItem(Properties settings, Spell spell) {
        super(settings);
		this.spell = spell;
    }

    @Override
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide() && !(player.getOffhandItem().getItem() instanceof SpellBookItem)) {
            player.getCooldowns().addCooldown(player.getItemInHand(hand), 5);
			List<Identifier> mutable = ModUtil.makeMutable(player.getAttached(ModData.EQUIPPED_SPELLS));
            SoundEvent sound = null;
            Identifier spellName = BuiltInRegistries.ITEM.getKey(this);
            if (player.isShiftKeyDown()) {
                if (mutable.remove(spellName))
                    sound = getSound(false);
            } else if (!mutable.contains(spellName)) {
                mutable.add(spellName);
                sound = getSound(true);
            }
            if (sound != null) world.playSound(null, player.blockPosition(), sound, SoundSource.PLAYERS);
			player.setAttached(ModData.EQUIPPED_SPELLS, mutable);
			return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    protected SoundEvent getSound(boolean add) {
        return add ? SoundEvents.RESPAWN_ANCHOR_CHARGE : SoundEvents.RESPAWN_ANCHOR_DEPLETE.value();
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof Player player && !Objects.equals(ModUtil.getInventoryStack(player, stack.getItem()), stack)) {
            player.sendOverlayMessage(Component.translatable("notify.lulasmod.duplicate_spell"));
            entity.spawnAtLocation(world, stack.copy());
            stack.shrink(stack.getCount());
        }
    }
}