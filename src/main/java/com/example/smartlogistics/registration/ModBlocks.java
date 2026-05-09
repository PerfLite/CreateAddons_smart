package com.example.smartlogistics.registration;

import com.example.smartlogistics.SmartLogistics;
import com.example.smartlogistics.block.HorizontalItemPusherBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SmartLogistics.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SmartLogistics.MODID);

    public static final DeferredBlock<HorizontalItemPusherBlock> HORIZONTAL_ITEM_PUSHER = BLOCKS.register(
        "horizontal_item_pusher",
        () -> new HorizontalItemPusherBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(3.5f)
            .noOcclusion())
    );

    public static final DeferredItem<BlockItem> HORIZONTAL_ITEM_PUSHER_ITEM = ITEMS.registerSimpleBlockItem(
        "horizontal_item_pusher", HORIZONTAL_ITEM_PUSHER
    );
}
