package net.spit365.lulasmod.custom.item.seal;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.spit365.lulasmod.custom.item.SpellItem;
import net.spit365.lulasmod.mod.ModTagCategories;
import net.spit365.lulasmod.tag.TagManager;

public class CatalystItem extends Item {
    public CatalystItem(Settings settings) {
        super(settings);
    }
    protected Boolean spellSelected(PlayerEntity player, Item item) {
        return item instanceof SpellItem && TagManager.readList(player, ModTagCategories.SPELLS).get(0).equals(Registries.ITEM.getId(item));
    }
}
