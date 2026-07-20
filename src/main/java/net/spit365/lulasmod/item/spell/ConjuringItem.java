package net.spit365.lulasmod.item.spell;

import net.spit365.lulasmod.util.Spell;

import static net.minecraft.sounds.SoundEvents.ZOMBIE_VILLAGER_CURE;

import net.minecraft.sounds.SoundEvent;

public class ConjuringItem extends SpellItem{
	public ConjuringItem(Properties settings, Spell conjuring) {
		super(settings, conjuring);
	}

	@Override
	protected SoundEvent getSound(boolean add) {
		return add ? ZOMBIE_VILLAGER_CURE : null;
	}
}
