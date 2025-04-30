package net.spit365.lulasmod.custom.item;

import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class IncantationItem extends SpellItem {
    public IncantationItem(Settings settings) {
        super(settings);
    }

    @Override protected int cooldown() {return 5;}
    @Override protected SoundEvent sound() {return SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE;}
}