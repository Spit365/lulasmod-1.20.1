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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.spit365.lulasmod.Server;
import net.spit365.lulasmod.mod.ModServer;
import net.spit365.lulasmod.manager.TagManager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class SpellPedestalBlock extends Block {
    public SpellPedestalBlock(Settings settings) {super(settings);}

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16.0, 12.0, 16.0);

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult hit){
        if (!world.isClient()){
            LinkedList<Identifier> absorbedPedestals = TagManager.readList(player, ModServer.TagCategories.ABSORBED_PEDESTALS);
            Identifier idPos = new Identifier(Server.MOD_ID, blockPos.getX() + "_" + blockPos.getY() + "_" + blockPos.getZ());
            if(!absorbedPedestals.contains(idPos) && absorbedPedestals.add(idPos)){
                 List<Identifier> spells = ModServer.Spells.SpellTabItems;
                 Set<Item> excluded = new HashSet<>(ModServer.Items.tailedExclusive);
                 excluded.add(ModServer.Spells.HIGHLIGHTER_SPELL);
                 spells.removeIf(id -> excluded.contains(Registries.ITEM.get(id)));
                 if (absorbedPedestals.size() <= spells.size()){
                      TagManager.put(player, ModServer.TagCategories.ABSORBED_PEDESTALS, absorbedPedestals);
                      ((ServerWorld) world).spawnParticles(ParticleTypes.CRIMSON_SPORE, blockPos.getX(), blockPos.getY(), blockPos.getZ(), 500, 1.5, 1.5, 1.5, 0);
                      player.giveItemStack(new ItemStack(Registries.ITEM.get(spells.get(absorbedPedestals.size() - 1))));
                 } else player.sendMessage(Text.translatable("notify.lulasmod.pedestal.all_spells"), true);
                 return ActionResult.success(true);
            }else player.sendMessage(Text.translatable("notify.lulasmod.already_absorbed_pedestal"), true);
        }
        return ActionResult.PASS;
    }
    @Override public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context){return SHAPE;}
}