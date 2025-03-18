package net.spit365.lulasmod.custom;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import com.mojang.brigadier.arguments.IntegerArgumentType;


import static net.minecraft.server.command.CommandManager.*;

public class EnchantMod implements ModInitializer {

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("enchant")
					.then(argument("enchantment", IdentifierArgumentType.identifier())
							.then(argument("level", IntegerArgumentType.integer(1, 500))
									.executes(context -> {
										Identifier enchantmentId = IdentifierArgumentType.getIdentifier(context, "enchantment");

										Enchantment enchantment = Registries.ENCHANTMENT.get(enchantmentId);

										// ✅ FIXED: Fehlerbehandlung für ungültige Verzauberung
										if (enchantment == null) {
											context.getSource().sendFeedback(() ->
													Text.literal("❌ Verzauberung " + enchantmentId + " nicht gefunden!")
															.formatted(Formatting.RED), false);
											return 0;
										}

										return enchantItem(context.getSource(), enchantment, IntegerArgumentType.getInteger(context, "level"));
									})
							)
					)
			);
		});
	}

	private int enchantItem(ServerCommandSource source, Enchantment enchantment, int level) {
		if (source.getEntity() instanceof ServerPlayerEntity player && !player.getMainHandStack().isEmpty()) {
			ItemStack stack = player.getMainHandStack();
			stack.addEnchantment(enchantment, level); // ✅ FIXED

			// ✅ FIXED: `getTranslationKev()` korrigiert zu `getTranslationKey()`
			String enchantmentName = enchantment.getName(1).getString();
			source.sendFeedback(() -> Text.literal("✅ Verzaubert mit " + enchantmentName + " (Stufe " + level + ")").formatted(Formatting.GREEN), false);
			return 1;
		}
		source.sendFeedback(() -> Text.literal("❌ Halte ein Item in der Hand!").formatted(Formatting.RED), false);
		return 0;
	}
}
