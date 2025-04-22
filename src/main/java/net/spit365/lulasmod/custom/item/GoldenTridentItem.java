package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.tag.TagManager;
import net.spit365.lulasmod.mod.ModTagCategories;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GoldenTridentItem extends TridentItem {
    public GoldenTridentItem(Settings settings) {
        super(settings);
    }

    public static final List<ArrowEntity> arrows = new LinkedList<>();

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
                TagManager.put(player, ModTagCategories.VICTIM, new Identifier(Lulasmod.MOD_ID, String.valueOf(selectedEntity.getId())));
                player.getItemCooldownManager().set(this, 200);
                if (!player.isCreative()) player.getStackInHand(hand).damage(100, player, p -> p.sendToolBreakStatus(player.getActiveHand()));
                return TypedActionResult.success(player.getStackInHand(hand));
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
    public static void impale(MinecraftServer server){
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()){
            Identifier read = TagManager.read(player, ModTagCategories.VICTIM);
            if (read != null){
                Entity victim = player.getWorld().getEntityById(Integer.parseInt(read.getPath()));
                if (victim != null && victim.isAlive()) {
                    victim.setVelocity(0, 0, 0);
                    Vec3d pos = new Vec3d(Math.random() * (new Random().nextBoolean() ? 1 : -1), Math.random() * (new Random().nextBoolean() ? 1 : -1), Math.random() * (new Random().nextBoolean() ? 1 : -1)).normalize().multiply(5).add(victim.getPos());
                    if (!(victim instanceof EndermanEntity)) {
                        ArrowEntity arrow = new ArrowEntity(victim.getWorld(), player);
                        arrows.add(arrow);
                        victim.getWorld().spawnEntity(arrow);
                        arrow.setNoGravity(true);
                        arrow.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                        arrow.setDamage(Double.MAX_VALUE);
                        TagManager.put(arrow, ModTagCategories.DAMAGE_DELAY, new Identifier(Lulasmod.MOD_ID, "0"));
                        arrow.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
                        arrow.setCritical(true);
                        arrow.addVelocity(pos.subtract(victim.getPos()).multiply(-0.5));
                    }else victim.kill();
                } else {for (ArrowEntity arrow : arrows) arrow.kill(); TagManager.remove(player, ModTagCategories.VICTIM);}
            }
        }
    }
}
