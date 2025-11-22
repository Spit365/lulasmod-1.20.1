package net.spit365.lulasmod.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.spit365.lulasmod.util.SpellHotbar;
import net.spit365.lulasmod.item.spell.SorceryItem;
import net.spit365.lulasmod.item.spell.SpellItem;
import net.spit365.lulasmod.mod.ModData;
import net.spit365.lulasmod.mod.ModMethods;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SealItem extends Item  implements SpellHotbar {
    public SealItem(Settings settings, Usable canUse, float efficiencyMultiplier, int cooldownDivisor) {
		super(settings);
		this.canUse = canUse;
		this.efficiencyMultiplier = efficiencyMultiplier;
		this.cooldownDivisor = cooldownDivisor;
	}
	public static final int NO_COOLDOWN_RESULT = 0;
	public static final int FAIL_RESULT = -1;
	public interface Usable{boolean accept(LivingEntity entity);}
    public final Usable canUse;
    public final float efficiencyMultiplier;
    public final int cooldownDivisor;

    @Override public List<Identifier> getHotbarList(PlayerEntity player){return player.getAttached(ModData.EQUIPPED_SPELLS);}
    @Override public void onCycle(PlayerEntity player){
		List<Identifier> mutable = ModMethods.makeMutable(player.getAttached(ModData.EQUIPPED_SPELLS));
		Collections.rotate(mutable, -1);
		player.setAttached(ModData.EQUIPPED_SPELLS, mutable);
	}

	@Override
	public void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		sealLogic(null, null, attacker, ModMethods.getHandFromStack(attacker, stack), (serverWorld, player, hand, spellItem) ->
			spellItem instanceof SorceryItem sorceryItem ? sorceryItem.sorcery.hitEntity(serverWorld, player, hand, target, efficiencyMultiplier, cooldownDivisor) : FAIL_RESULT);
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
		return sealLogic(ActionResult.SUCCESS, ActionResult.PASS, player, hand, (serverWorld, player1, hand1,spellItem) ->
			spellItem instanceof SorceryItem sorceryItem ? sorceryItem.sorcery.useEntity(serverWorld, player, hand, entity, efficiencyMultiplier, cooldownDivisor) : FAIL_RESULT);
	}

	@Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
		return sealLogic(ActionResult.SUCCESS, ActionResult.PASS, player, hand, (serverWorld, player1, hand1, spellItem) ->
			spellItem.spell.cast(serverWorld, player, hand, efficiencyMultiplier, cooldownDivisor));
    }

	@Override
	public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
		sealLogic(null, null, user, ModMethods.getHandFromStack(user, stack), (serverWorld, player, hand, spellItem) ->
			spellItem instanceof SorceryItem sorceryItem ? sorceryItem.sorcery.castTick(serverWorld, player, hand, remainingUseTicks, efficiencyMultiplier, cooldownDivisor) : FAIL_RESULT);
	}

	@Override
	public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
		return sealLogic(true, false, user, ModMethods.getHandFromStack(user, stack), (serverWorld, player, hand, spellItem) ->
			spellItem instanceof SorceryItem sorceryItem ? sorceryItem.sorcery.castStop(serverWorld, player, hand, remainingUseTicks, efficiencyMultiplier, cooldownDivisor) : FAIL_RESULT);
	}

	private <T> T sealLogic(T resultSuccess, T resultFail, LivingEntity user, Hand hand, SpellAction spellAction){
		if (user instanceof PlayerEntity player && player.getWorld() instanceof ServerWorld serverWorld && canUse.accept(player)) {
			List<Identifier> spellList = player.getAttached(ModData.EQUIPPED_SPELLS);
			if(spellList != null && !spellList.isEmpty() && Registries.ITEM.get(spellList.getFirst()) instanceof SpellItem spellItem){
				int result = spellAction.accept(serverWorld, player, hand, spellItem);
				return handleResult(resultSuccess, resultFail, hand, player, result);
			}
		}
		return resultFail;
	}

	private static <T> T handleResult(T resultSuccess, T resultFail, Hand hand, PlayerEntity player, int result) {
		return switch (result) {
			case NO_COOLDOWN_RESULT -> resultSuccess;
			case FAIL_RESULT -> resultFail;
			default -> {
				if (result > 0) {
					player.getItemCooldownManager().set(player.getStackInHand(hand), Math.max(result, 2));
					yield resultSuccess;
				}
				throw new IllegalArgumentException("Result integer from Spell must be >= -1, got " + result);
			}
		};
	}

	@FunctionalInterface private interface SpellAction{int accept(ServerWorld serverWorld, PlayerEntity player, Hand hand, SpellItem spellItem);}

	@Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if(entity instanceof PlayerEntity player && !Objects.equals(ModMethods.getInventoryStack(player, stack.getItem()), stack)) {
            player.sendMessage(Text.translatable("notify.lulasmod.duplicate_seal"), true);
            stack.decrement(stack.getCount());
        }
    }

	@Override
	public int getMaxUseTime(ItemStack stack, LivingEntity user) {
		List<Identifier> spells = user.getAttached(ModData.EQUIPPED_SPELLS);
		return spells != null && Registries.ITEM.get(spells.getFirst()) instanceof SorceryItem? 72000 : 0;
	}
}