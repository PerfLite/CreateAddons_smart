package com.example.smartlogistics.registration;

import com.example.smartlogistics.SmartLogistics;
import com.example.smartlogistics.blockentity.HorizontalItemPusherBlockEntity;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SmartLogistics.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HorizontalItemPusherBlockEntity>> HORIZONTAL_ITEM_PUSHER =
        BLOCK_ENTITIES.register(
            "horizontal_item_pusher",
            () -> BlockEntityType.Builder.of(
                HorizontalItemPusherBlockEntity::new,
                ModBlocks.HORIZONTAL_ITEM_PUSHER.get()
            ).build(null)
        );
}
