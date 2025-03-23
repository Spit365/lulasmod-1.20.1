package net.spit365.lulasmod.mod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.entity.SpellManager;


import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;
import static net.spit365.lulasmod.mod.ModItems.IncantationItems;

public class ModCommands {

    public static void init(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("spellslots")
                .then(CommandManager.argument("slots", IntegerArgumentType.integer(0, IncantationItems.size()))
                        .executes(context -> {
                            if (context.getSource().getPlayer() != null && context.getSource().getPlayer().isCreative()) {
                                SpellManager.setPlayerSpellSlots(Objects.requireNonNull(context.getSource().getPlayer()), IntegerArgumentType.getInteger(context, "slots"));
                                context.getSource().sendFeedback(() -> Text.literal(context.getSource().getName() + "'s Spellslots have been set to " + IntegerArgumentType.getInteger(context, "slots")), true);
                            }
                            return 1;
                        })
                )
            );
            dispatcher.register(literal("giveincs")
                .executes(context ->{
                    PlayerEntity player = context.getSource().getPlayer();
                    if (player != null && player.getCommandTags().contains("tailed")) for (Identifier id : IncantationItems) player.giveItemStack(new ItemStack(Registries.ITEM.get(id))); else context.getSource().sendFeedback(() -> Text.literal("You cannot use this action"), false);
                    return 1;
                })
            );
        });
    }
}
