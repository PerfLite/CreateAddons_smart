package com.example.smartlogistics.registration;

import com.example.smartlogistics.SmartLogistics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;

public class ModCreativeTab {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SmartLogistics.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register(
        "main",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.smartlogistics"))
            .icon(() -> new ItemStack(ModBlocks.HORIZONTAL_ITEM_PUSHER.get()))
            .displayItems((params, output) -> {
                output.accept(ModBlocks.HORIZONTAL_ITEM_PUSHER.get());
            })
            .build()
    );
}
