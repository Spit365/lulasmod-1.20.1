package net.spit365.lulasmod.custom.item;

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
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.SpellHotbar;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModMethods;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SealItem extends Item  implements SpellHotbar {
    public SealItem(Usable canUse, float efficiencyMultiplier, int cooldownDivisor) {
		super(new Item.Settings().maxCount(1));
		this.canUse = canUse;
		this.efficiencyMultiplier = efficiencyMultiplier;
		this.cooldownDivisor = cooldownDivisor;
	}
	public interface Usable{boolean accept(LivingEntity entity);}
    public Usable canUse;
    public Float efficiencyMultiplier;
    public Integer cooldownDivisor;

    @Override public List<Identifier> getHotbarList(PlayerEntity player){return player.getAttached(ModData.EQUIPPED_SPELLS);}
    @Override public void onCycle(PlayerEntity player){
		List<Identifier> list = player.getAttached(ModData.EQUIPPED_SPELLS);
		if (list != null) {
			Collections.rotate(list, -1);
			player.setAttached(ModData.EQUIPPED_SPELLS, list);
		}
	}


    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world instanceof ServerWorld serverWorld && canUse.accept(player)) {
            List<Identifier> spellList = player.getAttached(ModData.EQUIPPED_SPELLS);
            if(spellList != null && !spellList.isEmpty() && Registries.ITEM.get(spellList.getFirst()) instanceof SpellItem spellItem) {
                player.getItemCooldownManager().set(player.getStackInHand(hand), Math.max(spellItem.cooldown, 2));
                spellItem.cast(serverWorld, player, hand, efficiencyMultiplier, cooldownDivisor);
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }
    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if(entity instanceof PlayerEntity player && !Objects.equals(ModMethods.getInventoryStack(player, stack.getItem()), stack)) {
            player.sendMessage(Text.translatable("notify.lulasmod.duplicate_seal"), true);
            stack.decrement(stack.getCount());
        }
    }
}