package net.spit365.lulasmod.mod;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.spit365.lulasmod.manager.TagManager;
import java.util.*;
import static net.minecraft.server.command.CommandManager.literal;
import static net.spit365.lulasmod.mod.ModServer.Items.tailedExclusive;

public class ModCommands {
    public static void init(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            int r = 0;
            dispatcher.register(literal("contract")
                .executes(context ->{
                    PlayerEntity player = context.getSource().getPlayer();
                    if (player != null && player.getCommandTags().contains("tailed")){
                        Set<Boolean> booleanLinkedList = new HashSet<>();
                        for (Item item : tailedExclusive) {
                            boolean b = ModMethods.getItemStack(player, item) == null;
                            if (b) player.giveItemStack(new ItemStack(item));
                            booleanLinkedList.add(b);
                        }
                        if (booleanLinkedList.stream().allMatch(Boolean::booleanValue)) context.getSource().sendFeedback(() ->
                                Text.translatable("notify.lulasmod.command.contract_success"), false);
                    } else context.getSource().sendFeedback(() -> Text.translatable("notify.lulasmod.command.contract_fail"), false);
                    return r;
                })

            );
            dispatcher.register(literal("tag-manager")
                .then(literal("add").then(CommandManager.argument("category", StringArgumentType.word()).then(CommandManager.argument("namespace", StringArgumentType.word()).then(CommandManager.argument("value", StringArgumentType.word()).executes(commandContext -> {
                    TagManager.add(Objects.requireNonNull(commandContext.getSource().getEntity()), new TagManager.TagCategory(StringArgumentType.getString(commandContext, "category")), new Identifier(StringArgumentType.getString(commandContext, "namespace"), StringArgumentType.getString(commandContext, "value")));
                    commandContext.getSource().sendFeedback(() -> Text.translatable("notify.lulasmod.command.tag_manager_add", StringArgumentType.getString(commandContext, "category"), StringArgumentType.getString(commandContext, "value")), true);
                    return r;
                }))))).then(literal("put").then(CommandManager.argument("category", StringArgumentType.word()).then(CommandManager.argument("namespace", StringArgumentType.word()).then(CommandManager.argument("value", StringArgumentType.word()).executes(commandContext -> {
                    TagManager.put(Objects.requireNonNull(commandContext.getSource().getEntity()), new TagManager.TagCategory(StringArgumentType.getString(commandContext, "category")), new Identifier(StringArgumentType.getString(commandContext, "namespace"), StringArgumentType.getString(commandContext, "value")));
                    commandContext.getSource().sendFeedback(() -> Text.translatable("notify.lulasmod.command.tag_manager_put", StringArgumentType.getString(commandContext, "category"), StringArgumentType.getString(commandContext, "value")), true);
                    return r;
                }))))).then(literal("read").then(CommandManager.argument("category", StringArgumentType.word()).executes(commandContext -> {
                    commandContext.getSource().sendFeedback(() -> Text.translatable("notify.lulasmod.command.tag_manager_read", StringArgumentType.getString(commandContext, "category"), TagManager.read(Objects.requireNonNull(commandContext.getSource().getEntity()), new TagManager.TagCategory(StringArgumentType.getString(commandContext, "category")))), true);
                    return r;
                }))).then(literal("remove").then(CommandManager.argument("category", StringArgumentType.word()).executes(commandContext -> {
                    TagManager.remove(Objects.requireNonNull(commandContext.getSource().getEntity()), new TagManager.TagCategory(StringArgumentType.getString(commandContext, "category")));
                    commandContext.getSource().sendFeedback(() -> Text.translatable("notify.lulasmod.command.tag_manager_remove_all", StringArgumentType.getString(commandContext, "category")), true);
                    return r;
                    }).then(CommandManager.argument("namespace", StringArgumentType.word()).then(CommandManager.argument("value", StringArgumentType.word()).executes(commandContext -> {
                        TagManager.remove(Objects.requireNonNull(commandContext.getSource().getEntity()), new TagManager.TagCategory(StringArgumentType.getString(commandContext, "category")), new Identifier(StringArgumentType.getString(commandContext, "namespace"), StringArgumentType.getString(commandContext, "value")));
                        commandContext.getSource().sendFeedback(() -> Text.translatable("notify.lulasmod.command.tag_manager_remove_specific", StringArgumentType.getString(commandContext, "value"), StringArgumentType.getString(commandContext, "category")), true);
                        return r;
                }))))).then(literal("check").then(CommandManager.argument("category", StringArgumentType.word()).then(CommandManager.argument("namespace", StringArgumentType.word()).then(CommandManager.argument("value", StringArgumentType.word()).executes(commandContext -> {
                    commandContext.getSource().sendFeedback(() -> Text.translatable((TagManager.check(Objects.requireNonNull(commandContext.getSource().getEntity()), new TagManager.TagCategory(StringArgumentType.getString(commandContext, "category")), new Identifier(StringArgumentType.getString(commandContext, "namespace"), StringArgumentType.getString(commandContext, "value")))? "notify.lulasmod.command.tag_manager_check_success" : "notify.lulasmod.command.tag_manager_check_fail"), StringArgumentType.getString(commandContext, "category"), StringArgumentType.getString(commandContext, "value")), true);
                    return r;
                })))))
            );
            dispatcher.register(literal("applyBleed").then(CommandManager.argument("targets", EntityArgumentType.entities()).then(CommandManager.argument("seconds", IntegerArgumentType.integer()).executes(context ->  {
                for (Entity e : EntityArgumentType.getEntities(context, "targets")) if (e instanceof LivingEntity)
                    ModMethods.applyBleed((LivingEntity) e, IntegerArgumentType.getInteger(context, "seconds") * 20);
                return r;
            }))));
            dispatcher.register(literal("removeCooldown").executes(context -> {
                if (context.getSource().getEntity() instanceof PlayerEntity player) for (int i = 0; i < player.getInventory().size(); i++)
                    player.getItemCooldownManager().set(player.getInventory().getStack(i).getItem(), 0);
                return r;
            }));
        });
    }
}
