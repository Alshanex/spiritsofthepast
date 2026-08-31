package net.hazex.spiritsofthepast.datagen.Tags;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.registries.SotPItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class SotPItemTagsProvider extends ItemTagsProvider {
    public SotPItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, SpiritsofthePast.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {

        tag(SotPTags.Items.FOSSIL_REPAIR)
                .add(SotPItemRegistry.FOSSIL.get())
        ;

        tag(SotPTags.Items.MEDJAY_REPAIR)
                .add(Items.GOLD_INGOT)
        ;

        tag(ItemTags.SWORDS)
                .add(
                        SotPItemRegistry.AMBER_SPEAR.get(),
                        SotPItemRegistry.MEDJAY_SPEAR.get(),
                        SotPItemRegistry.FOSSILIZED_JAVELIN.get(),
                        SotPItemRegistry.KHOPESH.get()
                        )
        ;

        tag(ItemTags.SPEARS)
                .add(
                        SotPItemRegistry.AMBER_SPEAR.get(),
                        SotPItemRegistry.MEDJAY_SPEAR.get()
                        )
        ;

        tag(ItemTags.PICKAXES)
                .add(
                        SotPItemRegistry.AMBER_PICKAXE.get()
                        )
        ;

        tag(ItemTags.AXES)
                .add(
                        SotPItemRegistry.AMBER_AXE.get()
                        )
        ;

        tag(ItemTags.SHOVELS)
                .add(
                        SotPItemRegistry.AMBER_SHOVEL.get()
                        )
        ;

        tag(ItemTags.HOES)
                .add(
                        SotPItemRegistry.AMBER_SICKLE.get()
                        )
        ;

        tag(ItemTags.HEAD_ARMOR)
                .add(SotPItemRegistry.FOSSIL_HELMET.get())
                .add(SotPItemRegistry.MEDJAY_HELMET.get())
        ;

        tag(ItemTags.CHEST_ARMOR)
                .add(SotPItemRegistry.FOSSIL_CHESTPLATE.get())
                .add(SotPItemRegistry.MEDJAY_CHESTPLATE.get())
        ;

        tag(ItemTags.LEG_ARMOR)
                .add(SotPItemRegistry.FOSSIL_LEGGINGS.get())
                .add(SotPItemRegistry.MEDJAY_LEGGINGS.get())
        ;

        tag(ItemTags.FOOT_ARMOR)
                .add(SotPItemRegistry.FOSSIL_BOOTS.get())
                .add(SotPItemRegistry.MEDJAY_BOOTS.get())
        ;

    }
}