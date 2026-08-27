package net.hazex.spiritsofthepast.registries;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.blocks.EmptyTombBlockEntity;
import net.hazex.spiritsofthepast.blocks.PharaohsTombBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SotPBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpiritsofthePast.MODID);

    public static final Supplier<BlockEntityType<PharaohsTombBlockEntity>> PHARAOHS_TOMB_BE =
            BLOCK_ENTITIES.register("pharaohs_tomb", () -> new BlockEntityType<>(
                    PharaohsTombBlockEntity::new, false, SotPBlockRegistry.PHARAOHS_TOMB.get()));

    public static final Supplier<BlockEntityType<EmptyTombBlockEntity>> EMPTY_TOMB_BE =
            BLOCK_ENTITIES.register("pharaohs_tomb_empty", () -> new BlockEntityType<>(
                    EmptyTombBlockEntity::new, false, SotPBlockRegistry.EMPTY_TOMB.get()));

    public static void register(IEventBus modEventBus){
        BLOCK_ENTITIES.register(modEventBus);
    }
}
