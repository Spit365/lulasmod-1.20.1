package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.tag.TagManager;
import net.spit365.lulasmod.mod.ModTagCategories;

public class GoldenTridentItem extends TridentItem {
    public GoldenTridentItem(Settings settings) {
        super(settings);
    }



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
        if (!world.isClient()) {
            player.getItemCooldownManager().set(this, 2);
            double radius = 5;
            Vec3d selectionCenter = player.getRotationVec(1).normalize().multiply(radius).add(player.getPos());
            LivingEntity selectedEntity = null;
            for (Entity entity : world.getOtherEntities(player, new Box(selectionCenter.add(-radius, -radius, -radius), selectionCenter.add(radius, radius, radius)))){
                if ((selectedEntity == null || selectedEntity.getPos().squaredDistanceTo(player.getPos()) > entity.getPos().squaredDistanceTo(player.getPos())) && entity instanceof LivingEntity){
                    selectedEntity = (LivingEntity) entity;
                }
            }
            if (selectedEntity != null){
                selectedEntity.requestTeleport(selectedEntity.getX(), selectedEntity.getY() + 5, selectedEntity.getZ());
                TagManager.put(player, ModTagCategories.VICTIM, new Identifier("golden_shimmer", String.valueOf(selectedEntity.getId())));
                player.getItemCooldownManager().set(this, 200);
                if (!player.isCreative()) player.getStackInHand(hand).damage(100, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
                return TypedActionResult.success(player.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
