package net.spit365.lulasmod.mod;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.literal;
import static net.spit365.lulasmod.mod.ModItems.IncantationItems;

public class ModCommands {

    public static void init(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("contract")
            .executes(context ->{
                PlayerEntity player = context.getSource().getPlayer();
                if (player != null && player.getCommandTags().contains("tailed")){
                    player.giveItemStack(new ItemStack(ModItems.HELLISH_SEAL));
                    for (Identifier id : IncantationItems) player.giveItemStack(new ItemStack(Registries.ITEM.get(id)));
                }else context.getSource().sendFeedback(() -> Text.literal("You cannot use this action"), false);
                return 1;
            })
        ));
    }
}
