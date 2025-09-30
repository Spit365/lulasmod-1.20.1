package net.spit365.lulasmod.mod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.custom.item.spell.ConjuringItem;

import java.util.*;
import static net.minecraft.server.command.CommandManager.literal;

public class ModCommands {
    public static void init(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            int r = 0;
            dispatcher.register(literal("contract")
                .executes(context ->{
                    PlayerEntity player = context.getSource().getPlayer();
                    if (player != null && player.getCommandTags().contains("tailed")){
                        Set<Boolean> booleanLinkedList = new HashSet<>();
                        for (Identifier id : ModSpells.SpellTabItems) {
							Item item = Registries.ITEM.get(id);
							if (!(item instanceof ConjuringItem)) continue;
                            boolean b = ModMethods.getInventoryStack(player, item) == null;
                            if (b) player.giveItemStack(new ItemStack(item));
                            booleanLinkedList.add(b);
                        }
                        if (booleanLinkedList.stream().allMatch(Boolean::booleanValue)) context.getSource().sendFeedback(() ->
                                Text.translatable("notify.lulasmod.command.contract_success"), false);
                    } else context.getSource().sendFeedback(() -> Text.translatable("notify.lulasmod.command.contract_fail"), false);
                    return r;
                })

            );
            dispatcher.register(literal("applyBleed").then(CommandManager.argument("targets", EntityArgumentType.entities()).then(CommandManager.argument("seconds", IntegerArgumentType.integer()).executes(context ->  {
                for (Entity e : EntityArgumentType.getEntities(context, "targets")) if (e instanceof LivingEntity)
                    ModMethods.applyBleed((LivingEntity) e, IntegerArgumentType.getInteger(context, "seconds") * 20);
                return r;
            }))));
            dispatcher.register(literal("removeCooldown").executes(context -> {
                if (context.getSource().getEntity() instanceof PlayerEntity player) for (int i = 0; i < player.getInventory().size(); i++)
                    player.getItemCooldownManager().set(player.getInventory().getStack(i), 0);
                return r;
            }));
        });
    }
}
