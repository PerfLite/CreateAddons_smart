package com.example.smartlogistics.integration.jade;

import com.example.smartlogistics.blockentity.HorizontalItemPusherBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElementHelper;

public enum HorizontalItemPusherJadeProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlockEntity() instanceof HorizontalItemPusherBlockEntity be) {
            ItemStack filterStack = be.getFilterSlot().getStackInSlot(0);
            if (!filterStack.isEmpty()) {
                IElementHelper elements = IElementHelper.get();
                tooltip.add(Component.translatable("jade.smartlogistics.filter"));
                tooltip.append(elements.item(filterStack));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("smartlogistics", "horizontal_item_pusher");
    }
}
