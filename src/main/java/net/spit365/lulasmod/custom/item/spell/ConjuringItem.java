package net.spit365.lulasmod.custom.item.spell;

import net.minecraft.sound.SoundEvent;

import static net.minecraft.sound.SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE;

public class ConjuringItem extends SpellItem{
	public ConjuringItem(Settings settings, Spell spell) {
		super(settings, spell);
	}

	@Override
	protected SoundEvent getSound() {
		return ENTITY_ZOMBIE_VILLAGER_CURE;
	}
}
