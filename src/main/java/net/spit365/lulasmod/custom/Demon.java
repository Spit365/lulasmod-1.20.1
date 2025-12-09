package net.spit365.lulasmod.custom;

import net.minecraft.entity.Entity;
import net.spit365.lulasmod.mod.ModData;

public class Demon {
	public static void setDemon(Entity entity, boolean value) {
		entity.setAttached(ModData.DEMON, value);
	}

	public static boolean isDemon(Entity entity) {
		return Boolean.TRUE.equals(entity.getAttached(ModData.DEMON));
	}
}
