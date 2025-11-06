package net.spit365.lulasmod.item.spell;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;

import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class SorceryItem extends SpellItem{
	public final Sorcery sorcery;

	public SorceryItem(Settings settings, Sorcery sorcery) {
		super(settings, sorcery::cast);
		this.sorcery = sorcery;
	}

	public interface Sorcery{
		int hitEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, Float efficiencyMultiplier, Integer cooldownMultiplier);
		int useEntity(ServerWorld world, PlayerEntity player, Hand hand, LivingEntity target, Float efficiencyMultiplier, Integer cooldownMultiplier);
		int cast(ServerWorld world, PlayerEntity player, Hand hand, Float efficiencyMultiplier, Integer cooldownMultiplier);
		int castTick(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, Float efficiencyMultiplier, Integer cooldownMultiplier);
		int castStop(ServerWorld world, PlayerEntity player, Hand hand, int remainingUseTicks, Float efficiencyMultiplier, Integer cooldownMultiplier);
	}

	@Override
	protected SoundEvent getSound() {
		return SoundEvents.BLOCK_BEACON_ACTIVATE;
	}
}
