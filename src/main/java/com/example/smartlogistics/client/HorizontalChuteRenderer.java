package com.example.smartlogistics.client;

import com.example.smartlogistics.blockentity.HorizontalItemPusherBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

// Filter rendering removed - using GUI slot only
public class HorizontalChuteRenderer implements BlockEntityRenderer<HorizontalItemPusherBlockEntity> {

    public HorizontalChuteRenderer(BlockEntityRendererProvider.Context context) {}

    @Override
    public void render(HorizontalItemPusherBlockEntity be, float partialTick, com.mojang.blaze3d.vertex.PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // No filter rendering on block - use GUI slot instead
    }
}
