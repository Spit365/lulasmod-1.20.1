package net.spit365.lulasmod.item.spell;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.spit365.lulasmod.util.Spell;

public class SorceryItem extends SpellItem{
	public SorceryItem(Properties settings, Spell sorcery) {
		super(settings, sorcery);
	}

	@Override
	protected SoundEvent getSound(boolean add) {
		return add ? SoundEvents.BEACON_ACTIVATE : SoundEvents.BEACON_DEACTIVATE;
	}
}
