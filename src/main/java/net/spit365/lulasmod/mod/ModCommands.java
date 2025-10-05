package net.spit365.lulasmod.mod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;

import java.util.*;
import static net.minecraft.server.command.CommandManager.literal;

public class ModCommands {
    public static void init(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            int r = 0;
            dispatcher.register(literal("bleed")
				.then(CommandManager.argument("targets", EntityArgumentType.entities())
					.then(CommandManager.argument("seconds", IntegerArgumentType.integer())
						.executes(context ->  {
                			for (Entity e : EntityArgumentType.getEntities(context, "targets")) if (e instanceof LivingEntity)
                   				ModMethods.applyBleed((LivingEntity) e, IntegerArgumentType.getInteger(context, "seconds") * 20);
                			return r;
            }))));
            dispatcher.register(literal("removeCooldown")
				.executes(context -> {
                	if (context.getSource().getEntity() instanceof PlayerEntity player) for (int i = 0; i < player.getInventory().size(); i++)
                    	player.getItemCooldownManager().set(player.getInventory().getStack(i), 0);
                	return r;
            }));
        });
    }
}
