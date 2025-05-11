package net.spit365.lulasmod.custom.item.seal;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.spit365.lulasmod.custom.item.SpellItem;
import net.spit365.lulasmod.mod.ModTagCategories;
import net.spit365.lulasmod.tag.TagManager;

public abstract class SealItem extends Item {
    public SealItem() {super(new FabricItemSettings().maxCount(1));}

    protected abstract Boolean canUse(PlayerEntity player);
    protected abstract Float efficiencyMultiplier();
    protected abstract Integer cooldownMultiplier();

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world instanceof ServerWorld serverWorld && canUse(player)) {
            SpellItem equippedSpell = null;
            if(Registries.ITEM.get(TagManager.readList(player, ModTagCategories.SPELLS).get(0)) instanceof SpellItem spellItem) equippedSpell = spellItem;
            if (equippedSpell != null) {
                player.getItemCooldownManager().set(this, Math.max(equippedSpell.cooldown, 2));
                equippedSpell.execute(serverWorld, player, hand, efficiencyMultiplier(), cooldownMultiplier());
                player.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(player.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}