package net.hazex.spiritsofthepast.registries;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.blocks.cracked_sandstone.CrumblingSandstoneBlock;
import net.hazex.spiritsofthepast.blocks.tomb.EmptyTombBlock;
import net.hazex.spiritsofthepast.blocks.tomb.PharaohsTombBlock;
import net.hazex.spiritsofthepast.blocks.tomb.TombPartBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SotPBlockRegistry {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpiritsofthePast.MODID);

    public static final DeferredBlock<Block> SANDSTONE_BRICK = BLOCKS.registerBlock("sandstone_brick",
            properties -> new Block(properties
                    .mapColor(MapColor.SAND)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(0.8F)
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<Block> CRACKED_SANDSTONE_BRICK = BLOCKS.registerBlock("cracked_sandstone_brick",
            properties -> new Block(properties
                    .mapColor(MapColor.SAND)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(0.8F)
                    .sound(SoundType.STONE)
            ));

    public static final DeferredBlock<PharaohsTombBlock> PHARAOHS_TOMB = BLOCKS.registerBlock(
            "pharaohs_tomb",
            PharaohsTombBlock::new,
            props -> props
                    .mapColor(MapColor.SAND)
                    .strength(4.0F, 1200.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<EmptyTombBlock> EMPTY_TOMB = BLOCKS.registerBlock(
            "pharaohs_tomb_empty",
            EmptyTombBlock::new,
            props -> props
                    .mapColor(MapColor.SAND)
                    .strength(4.0F, 1200.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<TombPartBlock> TOMB_PART = BLOCKS.registerBlock(
            "pharaohs_tomb_part",
            TombPartBlock::new,
            props -> props
                    .mapColor(MapColor.SAND)
                    .strength(4.0F, 1200.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
                    .noOcclusion()
                    .noLootTable()
                    .dynamicShape()
                    .pushReaction(PushReaction.BLOCK));

    public static final DeferredBlock<CrumblingSandstoneBlock> CRUMBLING_SANDSTONE_BRICK = BLOCKS.registerBlock(
            "crumbling_sandstone_brick",
            CrumblingSandstoneBlock::new,
            props -> props
                    .mapColor(MapColor.SAND)
                    .strength(0.3F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops());

    public static void register(IEventBus modEventBus){
        BLOCKS.register(modEventBus);
    }
}
