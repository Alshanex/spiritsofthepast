package net.hazex.spiritsofthepast.datagen.Tags;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.registries.SotPBlockRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class SotPBlockTagsProvider extends BlockTagsProvider {
    public SotPBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, SpiritsofthePast.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(SotPBlockRegistry.CRACKED_SANDSTONE_BRICK.get())
                .add(SotPBlockRegistry.SANDSTONE_BRICK.get())
                .add(SotPBlockRegistry.CRUMBLING_SANDSTONE_BRICK.get())

             ;
    }
}