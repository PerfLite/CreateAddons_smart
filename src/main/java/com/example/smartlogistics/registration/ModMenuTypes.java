package com.example.smartlogistics.registration;

import com.example.smartlogistics.SmartLogistics;
import com.example.smartlogistics.blockentity.HorizontalItemPusherBlockEntity;
import com.example.smartlogistics.container.HorizontalItemPusherMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, SmartLogistics.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<HorizontalItemPusherMenu>> HORIZONTAL_ITEM_PUSHER_MENU =
        MENUS.register("horizontal_item_pusher_menu", () -> {
            IContainerFactory<HorizontalItemPusherMenu> factory = (id, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                CompoundTag tag = data.readNbt();
                var level = inv.player.level();
                if (level.getBlockEntity(pos) instanceof HorizontalItemPusherBlockEntity be) {
                    be.loadClient(tag, data.registryAccess());
                    return HorizontalItemPusherMenu.create(id, inv, be);
                }
                return null;
            };
            return new MenuType<HorizontalItemPusherMenu>(factory, net.minecraft.world.flag.FeatureFlags.DEFAULT_FLAGS);
        });
}
