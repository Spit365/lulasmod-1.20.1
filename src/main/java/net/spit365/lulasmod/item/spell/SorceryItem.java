package net.spit365.lulasmod.item.spell;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.spit365.lulasmod.item.SealItem;

public class SorceryItem extends SpellItem{
	public final Sorcery sorcery;

	public SorceryItem(Settings settings, Sorcery sorcery) {
		super(settings, sorcery);
		this.sorcery = sorcery;
	}

	public interface Sorcery extends Spell{
		default int hitEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, float efficiencyMultiplier, int cooldownDivisor) {return SealItem.FAIL_RESULT;}
		@Override
		default int cast(ServerWorld world, PlayerEntity player, Hand hand, float efficiencyMultiplier, int cooldownDivisor) {return SealItem.FAIL_RESULT;}
		default int castTick(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, float efficiencyMultiplier, int cooldownDivisor) {return SealItem.FAIL_RESULT;}
		default int castStop(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, float efficiencyMultiplier, int cooldownDivisor) {return SealItem.FAIL_RESULT;}
	}

	@Override
	protected SoundEvent getSound(boolean add) {
		return add ? SoundEvents.BLOCK_BEACON_ACTIVATE : SoundEvents.BLOCK_BEACON_DEACTIVATE;
	}
}
