package net.hazex.spiritsofthepast.datagen;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class SotPTags {

    public static class Blocks {
        public static final TagKey<Block> INCORRECT_FOR_FOSSIL_TOOL = createTag("incorrect_for_fossil_tool");


        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, name));
        }
    }


    public static class Items {


        public static final TagKey<Item> FOSSIL_REPAIR = createTag("fossil_repair");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, name));
        }


    }
}