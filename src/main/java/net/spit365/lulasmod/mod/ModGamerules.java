package net.spit365.lulasmod.mod;

import net.spit365.lulasmod.util.RegisterHelper;

import static net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory.createBooleanRule;

import net.minecraft.world.level.GameRules;

public final class ModGamerules {
	public static final GameRules.Key<GameRules.BooleanValue> NEW_DEATH_SYSTEM = RegisterHelper.gameRule("newDeathSystem", GameRules.Category.PLAYER, createBooleanRule(false));

	public static void init() {}
}
