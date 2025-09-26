package net.spit365.lulasmod.mod;

import net.minecraft.world.GameRules;
import net.spit365.lulasmod.manager.RegisterHelper;

public class ModGamerules {
	public static final GameRules.Key<GameRules.BooleanRule> NEW_DEATH_SYSTEM = RegisterHelper.gameRule("newDeathSystem", GameRules.Category.PLAYER, false);

	public static void init() {}
}
