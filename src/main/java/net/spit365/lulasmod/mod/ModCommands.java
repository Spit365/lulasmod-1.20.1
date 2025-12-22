package net.spit365.lulasmod.mod;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.Demon;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ModCommands {
    public static void init(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            int r = 0;
            dispatcher.register(literal("bleed")
				.then(argument("targets", EntityArgumentType.entities())
					.then(argument("seconds", IntegerArgumentType.integer())
						.executes(context ->  {
                			for (Entity e : EntityArgumentType.getEntities(context, "targets")) if (e instanceof LivingEntity)
                   				Bleed.apply((LivingEntity) e, IntegerArgumentType.getInteger(context, "seconds") * 20);
                			return r;
            }))));
            dispatcher.register(literal("removeCooldown")
				.executes(context -> {
                	if (context.getSource().getEntity() instanceof PlayerEntity player) for (int i = 0; i < player.getInventory().size(); i++)
                    	player.getItemCooldownManager().set(player.getInventory().getStack(i), 0);
                	return r;
            }));
			dispatcher.register(literal("demon")
				.then(argument("value", BoolArgumentType.bool())
					.executes(commandContext -> {
						Entity entity = commandContext.getSource().getEntity();
						if (entity != null) {
							boolean value = BoolArgumentType.getBool(commandContext, "value");
							Demon.setDemon(entity, value);
							commandContext.getSource().sendFeedback(() -> Text.translatable("notify.lulasmod.demon." + value, entity.getName()), true);
						}
						return r;
					})
					.then(argument("targets", EntityArgumentType.entities())
						.executes(commandContext -> {
							for (Entity entity : EntityArgumentType.getEntities(commandContext, "targets")) {
								if (entity != null) {
									boolean value = BoolArgumentType.getBool(commandContext, "value");
									Demon.setDemon(entity, value);
								}
							}
							return r;
						})
					)
				)
		    );
        });
    }

}
