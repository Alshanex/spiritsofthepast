package net.hazex.spiritsofthepast.datagen.Tags;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.registries.SotPEntityRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;

import java.util.concurrent.CompletableFuture;

public class SotPEntityTypeTagProvider extends EntityTypeTagsProvider {
    public SotPEntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, SpiritsofthePast.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {

        tag(EntityTypeTags.UNDEAD)
                .add(SotPEntityRegistry.PHARAOH.get())
        ;
        tag(EntityTypeTags.SKELETONS)
                .add(SotPEntityRegistry.PHARAOH.get())
        ;
        tag(EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES)
                .add(SotPEntityRegistry.PHARAOH.get())
        ;
        tag(EntityTypeTags.FALL_DAMAGE_IMMUNE)
                .add(SotPEntityRegistry.PHARAOH.get())
        ;
        tag(EntityTypeTags.INVERTED_HEALING_AND_HARM)
                .add(SotPEntityRegistry.PHARAOH.get())
        ;
        tag(EntityTypeTags.IGNORES_POISON_AND_REGEN)
                .add(SotPEntityRegistry.PHARAOH.get())
        ;
        tag(EntityTypeTags.SENSITIVE_TO_SMITE)
                .add(SotPEntityRegistry.PHARAOH.get())
        ;
        tag(EntityTypeTags.CANNOT_BE_PUSHED_ONTO_BOATS)
                .add(SotPEntityRegistry.PHARAOH.get())
        ;
        tag(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
                .add(SotPEntityRegistry.PHARAOH.get())
        ;


    }
}