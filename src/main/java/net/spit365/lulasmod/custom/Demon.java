package net.spit365.lulasmod.custom;

import net.minecraft.entity.Entity;
import net.spit365.lulasmod.mod.ModData;
import org.jetbrains.annotations.Nullable;

public class Demon {
	public static void setDemon(Entity entity, boolean value) {
		entity.setAttached(ModData.DEMON, value);
	}

	public static boolean isDemon(@Nullable Entity entity) {
		return entity != null && Boolean.TRUE.equals(entity.getAttached(ModData.DEMON));
	}
}
