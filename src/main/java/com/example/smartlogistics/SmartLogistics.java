package com.example.smartlogistics;

import com.example.smartlogistics.client.ClientSetup;
import com.example.smartlogistics.registration.ModBlocks;
import com.example.smartlogistics.registration.ModBlockEntities;
import com.example.smartlogistics.registration.ModCreativeTab;
import com.example.smartlogistics.registration.ModMenuTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@Mod(SmartLogistics.MODID)
public class SmartLogistics {

    public static final String MODID = "smartlogistics";

    public SmartLogistics(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENUS.register(modEventBus);
        ModCreativeTab.CREATIVE_TABS.register(modEventBus);

        modEventBus.addListener(this::registerCapabilities);

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(ClientSetup::onRegisterRenderers);
            modEventBus.addListener(ClientSetup::onMenuScreens);
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.HORIZONTAL_ITEM_PUSHER.get(),
            (be, context) -> be.getHeldItemHandler()
        );
    }
}
