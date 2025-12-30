package net.spit365.lulasmod.item.spell;

import net.minecraft.sound.SoundEvent;

import static net.minecraft.sound.SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE;

public class ConjuringItem extends SpellItem{
	public ConjuringItem(Settings settings, Spell spell) {
		super(settings, spell);
	}

	@Override
	protected SoundEvent getSound(boolean add) {
		return ENTITY_ZOMBIE_VILLAGER_CURE;
	}
}
