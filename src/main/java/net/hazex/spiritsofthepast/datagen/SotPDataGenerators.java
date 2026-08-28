package net.hazex.spiritsofthepast.datagen;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.datagen.Tags.SotPBlockTagsProvider;
import net.hazex.spiritsofthepast.datagen.Tags.SotPEntityTypeTagProvider;
import net.hazex.spiritsofthepast.datagen.Tags.SotPItemTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = SpiritsofthePast.MODID)
public class SotPDataGenerators {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();


        generator.addProvider(true, new SotPRecipeProvider.Runner(packOutput, lookupProvider));
        generator.addProvider(true, new SotPItemTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new SotPBlockTagsProvider(packOutput, lookupProvider));
        generator.addProvider(true, new SotPEntityTypeTagProvider(packOutput, lookupProvider));
    }
}