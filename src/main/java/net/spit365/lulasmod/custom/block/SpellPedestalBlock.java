package net.spit365.lulasmod.custom.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.mod.Mod;
import net.spit365.lulasmod.mod.ModMethods;
import net.spit365.lulasmod.tag.TagManager;

import java.util.LinkedList;
import java.util.List;

public class SpellPedestalBlock extends Block {
    public SpellPedestalBlock(Settings settings) {
        super(settings);
    }
    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if (!world.isClient()){
            LinkedList<Identifier> absorbedPedestals = TagManager.readList(player, Mod.TagCategories.ABSORBED_PEDESTALS);
            Identifier posAsId = new Identifier(Lulasmod.MOD_ID, pos.getX() + "_" + pos.getY() + "_" + pos.getZ());
            if (!absorbedPedestals.contains(posAsId)) {
                 absorbedPedestals.add(posAsId);
                 TagManager.put(player, Mod.TagCategories.ABSORBED_PEDESTALS, absorbedPedestals);
                 ((ServerWorld) world).spawnParticles(ParticleTypes.CRIMSON_SPORE, pos.getX(), pos.getY(), pos.getZ(), 500, 1.5, 1.5, 1.5, 0);
                 List<Identifier> spells = new LinkedList<>();
                 Mod.Spells.SpellTabItems.forEach(identifier -> {
                      Item item = Registries.ITEM.get(identifier);
                      if (!Mod.Items.tailedExclusive.contains(item)) spells.add(identifier);
                 });
                 for(Identifier id : spells){
                      Item spell = Registries.ITEM.get(id);
                      if(ModMethods.getItemStack(player, spell) == null) {
                          player.giveItemStack(new ItemStack(spell));
                          break;
                      }
                 }
                 return ActionResult.success(true);
            }else player.sendMessage(Text.translatable("notify.lulasmod.already_absorbed_pedestal"), true);
        }
        return ActionResult.PASS;
    }
}