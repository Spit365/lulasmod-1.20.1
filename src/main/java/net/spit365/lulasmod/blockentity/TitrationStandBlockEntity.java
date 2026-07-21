package net.spit365.lulasmod.blockentity;

import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.spit365.lulasmod.mod.ModEntities;
import net.spit365.lulasmod.screen.TitrationStandScreenHandler;
import net.spit365.lulasmod.util.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

public class TitrationStandBlockEntity extends BlockEntity implements ImplementedInventory, ExtendedMenuProvider<BlockPos> {
    public static NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);

    public TitrationStandBlockEntity(BlockPos pos, BlockState state) {
        super(ModEntities.TITRATION_STAND, pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer serverPlayerEntity) {
        return this.worldPosition;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.lulasmod.titration_stand");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new TitrationStandScreenHandler(syncId, playerInventory, this.worldPosition);
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        ContainerHelper.saveAllItems(view, inventory);
    }

    @Override
    protected void loadAdditional(ValueInput view) {
        super.loadAdditional(view);
        ContainerHelper.loadAllItems(view, inventory);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        return saveWithoutMetadata(registryLookup);
    }
}
