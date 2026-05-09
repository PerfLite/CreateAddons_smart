package com.example.smartlogistics.blockentity;

import com.example.smartlogistics.registration.ModBlockEntities;
import com.simibubi.create.content.logistics.filter.FilterItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class HorizontalItemPusherBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler filterSlot = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            filter = FilterItemStack.of(getStackInSlot(0));
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    private FilterItemStack filter = FilterItemStack.empty();
    private ItemStack heldItem = ItemStack.EMPTY;
    private final ItemStackHandler heldItemHandler = new ItemStackHandler(1) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            // Enforce filter on external insert (hopper, etc.)
            if (level != null && !filter.test(level, stack)) return stack;
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            heldItem = getStackInSlot(0);
            setChanged();
            if (level != null && !level.isClientSide) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public int getSlotLimit(int slot) { return 64; }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) { return true; }
    };
    private int cooldown;

    public HorizontalItemPusherBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.HORIZONTAL_ITEM_PUSHER.get(), pos, state);
    }

    public ItemStackHandler getFilterSlot() { return filterSlot; }
    public FilterItemStack getFilter() { return filter; }
    public ItemStack getHeldItem() { return heldItem; }
    public ItemStackHandler getHeldItemHandler() { return heldItemHandler; }

    public static void tick(Level level, BlockPos pos, BlockState state, HorizontalItemPusherBlockEntity be) {
        if (level.isClientSide) return;

        boolean powered = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED);
        Direction facing = state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);

        if (!be.heldItem.isEmpty()) {
            be.tryPushOrSpit(facing);
            if (be.heldItem.isEmpty()) be.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
            return;
        }

        if (be.cooldown > 0) { be.cooldown--; return; }
        if (powered) return;

        be.tryPullFrom(facing.getOpposite());
    }

    private void tryPullFrom(Direction back) {
        if (level == null) return;
        BlockPos sourcePos = worldPosition.relative(back);
        IItemHandler source = level.getCapability(Capabilities.ItemHandler.BLOCK, sourcePos, back.getOpposite());
        if (source == null) return;

        int toPull = 64;
        if (!heldItem.isEmpty()) {
            toPull = Math.min(64, heldItem.getMaxStackSize() - heldItem.getCount());
            if (toPull <= 0) return;
        }

        boolean pulled = false;
        for (int i = 0; i < source.getSlots() && toPull > 0; i++) {
            ItemStack extracted = source.extractItem(i, toPull, true);
            if (extracted.isEmpty()) continue;
            if (!filter.test(level, extracted)) continue;
            if (!heldItem.isEmpty() && !ItemStack.isSameItemSameComponents(heldItem, extracted)) continue;

            ItemStack real = source.extractItem(i, toPull, false);
            if (!real.isEmpty()) {
                if (heldItem.isEmpty()) {
                    updateHeldItem(real.copy());
                } else {
                    ItemStack newHeld = heldItem.copy();
                    newHeld.grow(real.getCount());
                    updateHeldItem(newHeld);
                }
                toPull -= real.getCount();
                pulled = true;
            }
        }
        if (pulled) {
            cooldown = 8;
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private void tryPushOrSpit(Direction facing) {
        if (level == null) return;
        BlockPos targetPos = worldPosition.relative(facing);
        IItemHandler target = level.getCapability(Capabilities.ItemHandler.BLOCK, targetPos, facing.getOpposite());

        if (target != null) {
            boolean anyInserted = false;
            for (int i = 0; i < target.getSlots(); i++) {
                ItemStack remainder = target.insertItem(i, heldItem.copy(), false);
                if (remainder.getCount() < heldItem.getCount()) {
                    anyInserted = true;
                    updateHeldItem(remainder);
                    if (heldItem.isEmpty()) return;
                }
            }
            if (anyInserted) return;
        }

        // No valid inventory or inventory full — spit out as entity ONLY if not blocked by solid block
        BlockPos frontPos = worldPosition.relative(facing);
        BlockState frontState = level.getBlockState(frontPos);
        if (!frontState.isSolid() && !heldItem.isEmpty()) {
            Vec3 spitPos = Vec3.atCenterOf(worldPosition).add(facing.getStepX() * 0.6, facing.getStepY() * 0.6, facing.getStepZ() * 0.6);
            ItemEntity entity = new ItemEntity(level, spitPos.x, spitPos.y, spitPos.z, heldItem.copy());
            entity.setDeltaMovement(facing.getStepX() * 0.1, 0.05, facing.getStepZ() * 0.1);
            level.addFreshEntity(entity);
            updateHeldItem(ItemStack.EMPTY);
        }
    }

    private void updateHeldItem(ItemStack stack) {
        this.heldItem = stack.copy();
        this.heldItemHandler.setStackInSlot(0, stack.copy());
        setChanged();
    }

    public boolean isStillValid(Player player) {
        if (level == null) return false;
        return level.getBlockEntity(worldPosition) == this && player.distanceToSqr(worldPosition.getCenter()) < 64.0;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.smartlogistics.horizontal_item_pusher");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInv, Player player) {
        return com.example.smartlogistics.container.HorizontalItemPusherMenu.create(id, playerInv, this);
    }

    public void openMenu(Player player) {
        if (level != null && !level.isClientSide && player instanceof ServerPlayer sp) {
            sp.openMenu(this, buf -> {
                buf.writeBlockPos(worldPosition);
                buf.writeNbt(getUpdateTag(level.registryAccess()));
            });
        }
    }

    public void loadClient(CompoundTag tag, HolderLookup.Provider registries) {
        loadAdditional(tag, registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        if (pkt.getTag() != null) {
            loadAdditional(pkt.getTag(), lookupProvider);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        super.handleUpdateTag(tag, registries);
        loadAdditional(tag, registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FilterSlot", filterSlot.serializeNBT(registries));
        if (!heldItem.isEmpty()) tag.put("HeldItem", heldItem.save(registries));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        filterSlot.deserializeNBT(registries, tag.getCompound("FilterSlot"));
        filter = FilterItemStack.of(filterSlot.getStackInSlot(0));
        heldItem = tag.contains("HeldItem") ? ItemStack.parse(registries, tag.getCompound("HeldItem")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        heldItemHandler.setStackInSlot(0, heldItem.copy());
    }
}
