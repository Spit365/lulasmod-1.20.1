package net.spit365.lulasmod.mod;

import net.minecraft.world.GameRules;
import net.spit365.lulasmod.util.RegisterHelper;

import static net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory.*;

public final class ModGamerules {
	public static final GameRules.Key<GameRules.BooleanRule> NEW_DEATH_SYSTEM = RegisterHelper.gameRule("newDeathSystem", GameRules.Category.PLAYER, createBooleanRule(false));

	public static void init() {}
}
