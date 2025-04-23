package net.spit365.lulasmod.custom.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.spit365.lulasmod.Lulasmod;
import net.spit365.lulasmod.custom.entity.GoldenProjectileEntity;
import net.spit365.lulasmod.mod.ModParticles;
import net.spit365.lulasmod.tag.TagManager;
import net.spit365.lulasmod.mod.ModTagCategories;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class GoldenTridentItem extends TridentItem {
    public GoldenTridentItem(Settings settings) {
        super(settings);
    }

    public static final List<GoldenProjectileEntity> golden_projectiles = new LinkedList<>();

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
        for (GoldenProjectileEntity g : golden_projectiles) ((ServerWorld) g.getWorld()).spawnParticles(ModParticles.GOLDEN_SHIMMER, g.getX(), g.getY(), g.getZ(), 2, 0.0625, 0.0625, 0.0625, 0);
        for (PlayerEntity player : server.getPlayerManager().getPlayerList()){
            Identifier read = TagManager.read(player, ModTagCategories.VICTIM);
            if (read != null){
                Entity victim = player.getWorld().getEntityById(Integer.parseInt(read.getPath()));
                if (victim != null && victim.isAlive()) {
                    victim.setVelocity(0, 0, 0);
                    Random random = new Random();
                    Vec3d pos = new Vec3d(Math.random() * (random.nextBoolean()? 1 : -1), Math.random() * (random.nextBoolean()? 1 : -1), Math.random() * (random.nextBoolean()? 1 : -1)).normalize().multiply(5).add(victim.getPos());
                    if (!(victim instanceof EndermanEntity)) {
                        GoldenProjectileEntity projectile = new GoldenProjectileEntity(victim.getWorld(), player);
                        golden_projectiles.add(projectile);
                        projectile.setNoGravity(true);
                        projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                        TagManager.put(projectile, ModTagCategories.DAMAGE_DELAY, new Identifier(Lulasmod.MOD_ID, "0"));
                        projectile.requestTeleport(pos.getX(), pos.getY(), pos.getZ());
                        projectile.addVelocity(pos.subtract(victim.getPos()).multiply(-0.5));
                        victim.getWorld().spawnEntity(projectile);
                    }else victim.kill();
                } else {for (GoldenProjectileEntity g : golden_projectiles) g.kill(); golden_projectiles.clear(); TagManager.remove(player, ModTagCategories.VICTIM);}
            }
        }
    }
}
