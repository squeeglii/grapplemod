package com.yyon.grapplinghook.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.blockentity.GrappleModifierBlockEntity;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class GrappleModBlockEntities {

    private static HashMap<ResourceLocation, GrappleModBlockEntities.BlockEntityEntry<?>> blockEntities;

    static {
        GrappleModBlockEntities.blockEntities = new HashMap<>();
    }

    public static <E extends BlockEntityType<?>> BlockEntityEntry<E> blockEntity(String id, Supplier<E> type) {
        ResourceLocation qualId = GrappleMod.id(id);
        BlockEntityEntry<E> entry = new BlockEntityEntry<>(qualId, type);
        GrappleModBlockEntities.blockEntities.put(qualId, entry);
        return entry;
    }


    public static void registerAllBlockEntities() {
        for(Map.Entry<ResourceLocation, BlockEntityEntry<?>> def: blockEntities.entrySet()) {
            ResourceLocation id = def.getKey();
            BlockEntityEntry<?> data = def.getValue();
            BlockEntityType<?> it = data.getFactory().get();

            data.finalize(Registry.register(Registry.BLOCK_ENTITY_TYPE, id, it));
        }
    }

    public static final BlockEntityEntry<BlockEntityType<GrappleModifierBlockEntity>> GRAPPLE_MODIFIER = GrappleModBlockEntities
            .blockEntity("block_grapple_modifier",() -> FabricBlockEntityTypeBuilder
                    .create(GrappleModifierBlockEntity::new, GrappleModBlocks.GRAPPLE_MODIFIER.get())
                    .build());


    public static class BlockEntityEntry<T extends BlockEntityType<?>> extends AbstractRegistryReference<T> {

        protected BlockEntityEntry(ResourceLocation id, Supplier<T> factory) {
            super(id, factory);
        }
    }

}


