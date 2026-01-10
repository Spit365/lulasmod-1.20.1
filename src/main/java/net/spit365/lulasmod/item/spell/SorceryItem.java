package net.spit365.lulasmod.item.spell;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.spit365.lulasmod.util.Spell;

public class SorceryItem extends SpellItem{
	public SorceryItem(Settings settings, Spell sorcery) {
		super(settings, sorcery);
	}

	@Override
	protected SoundEvent getSound(boolean add) {
		return add ? SoundEvents.BLOCK_BEACON_ACTIVATE : SoundEvents.BLOCK_BEACON_DEACTIVATE;
	}
}
