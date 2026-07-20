package net.spit365.lulasmod.mod;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.spit365.lulasmod.custom.Bleed;
import net.spit365.lulasmod.custom.Demon;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class ModCommands {
    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            int r = 0;
            dispatcher.register(literal("bleed")
				.then(argument("targets", EntityArgument.entities())
					.then(argument("seconds", IntegerArgumentType.integer())
						.executes(context ->  {
                			for (Entity e : EntityArgument.getEntities(context, "targets")) if (e instanceof LivingEntity)
                   				Bleed.apply((LivingEntity) e, IntegerArgumentType.getInteger(context, "seconds") * 20);
                			return r;
            }))));
            dispatcher.register(literal("removecooldown")
				.executes(context -> {
                	if (context.getSource().getEntity() instanceof Player player) for (int i = 0; i < player.getInventory().getContainerSize(); i++)
                    	player.getCooldowns().addCooldown(player.getInventory().getItem(i), 0);
                	return r;
            }));
			dispatcher.register(literal("demon")
				.then(argument("value", BoolArgumentType.bool())
					.executes(commandContext -> {
						Entity entity = commandContext.getSource().getEntity();
						setDemon(commandContext, entity);
						return r;
					})
					.then(argument("targets", EntityArgument.entities())
						.executes(commandContext -> {
							for (Entity entity : EntityArgument.getEntities(commandContext, "targets")) {
								setDemon(commandContext, entity);
							}
							return r;
						})
					)
				)
		    );
			dispatcher.register(literal("presence")
				.then(argument("value", IntegerArgumentType.integer(0))
					.executes(context -> {
						ServerPlayer player = context.getSource().getPlayer();
						if (player != null)
                            player.setAttached(ModData.PRESENCE_LEVEL, IntegerArgumentType.getInteger(context, "value"));
						return r;
					})
				)
			);
        });
    }

	private static void setDemon(CommandContext<CommandSourceStack> commandContext, Entity entity) {
		if (entity != null) {
			boolean value = BoolArgumentType.getBool(commandContext, "value");
			Demon.setDemon(entity, value);
			commandContext.getSource().sendSuccess(() -> Component.translatable("notify.lulasmod.demon." + value, entity.getName()), true);
		}
	}
}
