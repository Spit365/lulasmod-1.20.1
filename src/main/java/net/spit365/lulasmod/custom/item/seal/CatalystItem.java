package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.spit365.lulasmod.custom.item.IncantationItem;
import net.spit365.lulasmod.mod.ModTagCategories;
import net.spit365.lulasmod.tag.TagManager;

public abstract class CatalystItem extends Item {

    protected abstract Boolean canUse(PlayerEntity player);
    protected abstract Float efficiencyMultiplier();
    protected abstract Integer cooldownMultiplier();

    public CatalystItem(Settings settings) {
        super(settings);
    }
    protected Boolean spellSelected(PlayerEntity player, Item item) {
        return item instanceof IncantationItem && TagManager.readList(player, ModTagCategories.SPELLS).get(0).equals(Registries.ITEM.getId(item));
    }
    protected Boolean spellSelected(PlayerEntity player, Item item, Integer cooldown) {
        boolean bl = spellSelected(player, item);
        if (bl) player.getItemCooldownManager().set(this, cooldown / cooldownMultiplier());
        return bl;
    }
}