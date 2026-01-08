package net.spit365.lulasmod.item.spell;

import net.minecraft.sound.SoundEvent;
import net.spit365.lulasmod.util.Spell;

import static net.minecraft.sound.SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE;

public class ConjuringItem extends SpellItem{
	public ConjuringItem(Settings settings, Spell conjuring) {
		super(settings, conjuring);
	}

	@Override
	protected SoundEvent getSound(boolean add) {
		return add ? ENTITY_ZOMBIE_VILLAGER_CURE : null;
	}
}
