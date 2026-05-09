package com.example.smartlogistics.container;

import com.example.smartlogistics.blockentity.HorizontalItemPusherBlockEntity;
import com.example.smartlogistics.registration.ModMenuTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.minecraft.world.item.ItemStack;
import com.simibubi.create.content.logistics.filter.FilterItem;
import net.minecraft.world.level.block.entity.BlockEntity;

public class HorizontalItemPusherMenu extends AbstractContainerMenu {

    public Player player;
    public HorizontalItemPusherBlockEntity contentHolder;

    public HorizontalItemPusherMenu(int id, Inventory inv, RegistryFriendlyByteBuf extraData) {
        super(ModMenuTypes.HORIZONTAL_ITEM_PUSHER_MENU.get(), id);
        init(inv, createOnClient(extraData));
    }

    public HorizontalItemPusherMenu(int id, Inventory inv, HorizontalItemPusherBlockEntity be) {
        super(ModMenuTypes.HORIZONTAL_ITEM_PUSHER_MENU.get(), id);
        init(inv, be);
    }

    private void init(Inventory inv, HorizontalItemPusherBlockEntity be) {
        player = inv.player;
        contentHolder = be;
        if (be != null) {
            // Ghost slot - max stack size 1, cannot pick up, copies on place
            this.addSlot(new SlotItemHandler(be.getFilterSlot(), 0, 80, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return true;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return true;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }

                @Override
                public void set(ItemStack stack) {
                    if (!stack.isEmpty()) {
                        ItemStack copy = stack.copy();
                        copy.setCount(1);
                        super.set(copy);
                    } else {
                        super.set(ItemStack.EMPTY);
                    }
                }
            });
        }

        // Player inventory (3 rows x 9 slots) - matches hopper GUI layout
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9, 8 + col * 18, 51 + row * 18));
            }
        }

        // Player hotbar - matches hopper GUI layout
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col, 8 + col * 18, 109));
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId == 0) {
            if (clickType == ClickType.PICKUP) {
                ItemStack carried = this.getCarried();
                Slot filterSlot = this.slots.get(0);
                ItemStack currentFilter = filterSlot.getItem();

                if (!carried.isEmpty()) {
                    ItemStack copy = carried.copy();
                    copy.setCount(1);
                    filterSlot.set(copy);
                    filterSlot.setChanged();
                    // FilterItem costs 1 from hand; regular items are free (ghost)
                    if (carried.getItem() instanceof FilterItem && !player.isCreative()) {
                        if (carried.getCount() == 1) {
                            this.setCarried(ItemStack.EMPTY);
                        } else {
                            carried.shrink(1);
                        }
                    }
                } else if (!currentFilter.isEmpty()) {
                    // Return FilterItem to hand; regular items are ghost and disappear
                    if (currentFilter.getItem() instanceof FilterItem) {
                        this.setCarried(currentFilter.copy());
                    }
                    filterSlot.set(ItemStack.EMPTY);
                    filterSlot.setChanged();
                }
                return;
            } else if (clickType == ClickType.QUICK_MOVE) {
                return;
            }
        }
        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();

        if (index == 0) {
            // Moving FROM filter slot
            if (stack.getItem() instanceof FilterItem) {
                // Try to move FilterItem to player inventory
                if (!this.moveItemStackTo(stack.copy(), 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            slot.set(ItemStack.EMPTY);
            slot.setChanged();
            return ItemStack.EMPTY;
        }

        // Moving TO filter slot (index 0)
        Slot filterSlot = this.slots.get(0);
        if (filterSlot.getItem().isEmpty()) {
            ItemStack copy = stack.copy();
            copy.setCount(1);
            filterSlot.set(copy);
            filterSlot.setChanged();
            // FilterItem costs 1 from inventory; regular items are free (ghost)
            if (stack.getItem() instanceof FilterItem && !player.isCreative()) {
                stack.shrink(1);
                if (stack.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }
        }

        return ItemStack.EMPTY;
    }

    private HorizontalItemPusherBlockEntity createOnClient(RegistryFriendlyByteBuf extraData) {
        BlockPos pos = extraData.readBlockPos();
        CompoundTag tag = extraData.readNbt();

        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) return null;

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof HorizontalItemPusherBlockEntity be) {
            if (tag != null) {
                be.loadClient(tag, extraData.registryAccess());
            }
            return be;
        }
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return contentHolder != null && contentHolder.isStillValid(player);
    }

    public static HorizontalItemPusherMenu create(int id, Inventory inv, HorizontalItemPusherBlockEntity be) {
        return new HorizontalItemPusherMenu(id, inv, be);
    }
}
