package com.example.smartlogistics.client;

import com.example.smartlogistics.registration.ModBlockEntities;
import com.example.smartlogistics.registration.ModMenuTypes;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public class ClientSetup {

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.HORIZONTAL_ITEM_PUSHER.get(), HorizontalChuteRenderer::new);
    }

    public static void onMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.HORIZONTAL_ITEM_PUSHER_MENU.get(), HorizontalItemPusherScreen::new);
    }
}
