package net.spit365.lulasmod.custom.item.seal;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.custom.item.SpellItem;
import net.spit365.lulasmod.mod.ModServer;
import net.spit365.lulasmod.mod.ModMethods;
import net.spit365.lulasmod.manager.TagManager;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Objects;

public abstract class AbstractSealItem extends Item  implements SpellHotbar {
    public AbstractSealItem() {super(new Item.Settings().maxCount(1));}

    @Override public LinkedList<Identifier> displayList(PlayerEntity player){return TagManager.readList(player, ModServer.TagCategories.EQUIPPED_SPELLS);}
    @Override public void cycleList(PlayerEntity player){TagManager.cycle(player, ModServer.TagCategories.EQUIPPED_SPELLS);}

    public abstract Boolean canUse(LivingEntity entity);
    public abstract Float efficiencyMultiplier();
    public abstract Integer cooldownMultiplier();

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world instanceof ServerWorld serverWorld && canUse(player)) {
            LinkedList<Identifier> spellList = TagManager.readList(player, ModServer.TagCategories.EQUIPPED_SPELLS);
            if(!spellList.isEmpty() && Registries.ITEM.get(spellList.get(0)) instanceof SpellItem spellItem) {
                player.getItemCooldownManager().set(player.getStackInHand(hand), Math.max(spellItem.cooldown, 2));
                spellItem.cast(serverWorld, player, hand, efficiencyMultiplier(), cooldownMultiplier());
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if(entity instanceof PlayerEntity player && !Objects.equals(ModMethods.getItemStack(player, stack.getItem()), stack)) {
            player.sendMessage(Text.translatable("notify.lulasmod.duplicate_seal"), true);
            stack.decrement(stack.getCount());
        }
    }
}