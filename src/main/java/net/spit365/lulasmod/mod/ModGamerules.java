package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.spit365.lulasmod.util.RegisterHelper;

public final class ModGamerules {
	public static final GameRule<Boolean> NEW_DEATH_SYSTEM =
		RegisterHelper.gameRule("new_death_system", () -> GameRuleBuilder.forBoolean(false).category(GameRuleCategory.PLAYER));

	public static void init() {}
}
