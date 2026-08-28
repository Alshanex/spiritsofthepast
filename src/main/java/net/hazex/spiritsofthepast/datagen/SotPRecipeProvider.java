package net.hazex.spiritsofthepast.datagen;

import net.hazex.spiritsofthepast.registries.SotPBlockRegistry;
import net.hazex.spiritsofthepast.registries.SotPItemRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class SotPRecipeProvider extends RecipeProvider {
    public SotPRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new SotPRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "TutorialMod Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, SotPBlockRegistry.SANDSTONE_BRICK.get().asItem(), Items.SANDSTONE);
        stonecutterResultFromBase(RecipeCategory.BUILDING_BLOCKS, SotPBlockRegistry.CRACKED_SANDSTONE_BRICK.get().asItem(), Items.SANDSTONE);

        /*
        *** Weapons
         */

        shaped(RecipeCategory.COMBAT, SotPItemRegistry.FOSSILIZED_JAVELIN.get())
                .pattern("  F")
                .pattern(" F ")
                .pattern("I  ")
                .define('F', SotPItemRegistry.FOSSIL.get())
                .define('I', Items.IRON_SPEAR)
                .unlockedBy(getHasName(SotPItemRegistry.FOSSIL.get()), has(SotPItemRegistry.FOSSIL))
                .group("fossil")
                .save(output);

        shaped(RecipeCategory.COMBAT, SotPItemRegistry.AMBER_SPEAR.get())
                .pattern(" AF")
                .pattern(" FA")
                .pattern("D  ")
                .define('F', SotPItemRegistry.FOSSIL.get())
                .define('A', SotPItemRegistry.AMBER.get())
                .define('D', Items.DIAMOND_SPEAR)
                .unlockedBy(getHasName(SotPItemRegistry.FOSSIL.get()), has(SotPItemRegistry.FOSSIL))
                .group("fossil")
                .save(output);

        /*
        *** Armor
         */

        shaped(RecipeCategory.COMBAT, SotPItemRegistry.FOSSIL_HELMET.get())
                .pattern("FAF")
                .pattern("LDL")
                .pattern("   ")
                .define('F', SotPItemRegistry.FOSSIL.get())
                .define('A', SotPItemRegistry.AMBER.get())
                .define('D', Items.DIAMOND_HELMET)
                .define('L', Items.LEATHER)
                .unlockedBy(getHasName(SotPItemRegistry.FOSSIL.get()), has(SotPItemRegistry.FOSSIL))
                .group("fossil")
                .save(output);

        shaped(RecipeCategory.COMBAT, SotPItemRegistry.FOSSIL_CHESTPLATE.get())
                .pattern("F F")
                .pattern("ADA")
                .pattern("LFL")
                .define('F', SotPItemRegistry.FOSSIL.get())
                .define('A', SotPItemRegistry.AMBER.get())
                .define('D', Items.DIAMOND_CHESTPLATE)
                .define('L', Items.LEATHER)
                .unlockedBy(getHasName(SotPItemRegistry.FOSSIL.get()), has(SotPItemRegistry.FOSSIL))
                .group("fossil")
                .save(output);

        shaped(RecipeCategory.COMBAT, SotPItemRegistry.FOSSIL_LEGGINGS.get())
                .pattern("FDF")
                .pattern("FAF")
                .pattern("L L")
                .define('F', SotPItemRegistry.FOSSIL.get())
                .define('A', SotPItemRegistry.AMBER.get())
                .define('D', Items.DIAMOND_LEGGINGS)
                .define('L', Items.LEATHER)
                .unlockedBy(getHasName(SotPItemRegistry.FOSSIL.get()), has(SotPItemRegistry.FOSSIL))
                .group("fossil")
                .save(output);

        shaped(RecipeCategory.COMBAT, SotPItemRegistry.FOSSIL_BOOTS.get())
                .pattern("   ")
                .pattern("GAG")
                .pattern("FDF")
                .define('F', SotPItemRegistry.FOSSIL.get())
                .define('A', SotPItemRegistry.AMBER.get())
                .define('D', Items.DIAMOND_BOOTS)
                .define('G', Items.GOLD_INGOT)
                .unlockedBy(getHasName(SotPItemRegistry.FOSSIL.get()), has(SotPItemRegistry.FOSSIL))
                .group("fossil")
                .save(output);

    }
}