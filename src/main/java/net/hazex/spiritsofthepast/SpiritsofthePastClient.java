package net.hazex.spiritsofthepast;

import com.geckolib.renderer.GeoBlockRenderer;
import net.hazex.spiritsofthepast.entities.javelin.FossilizedJavelinRenderer;
import net.hazex.spiritsofthepast.entities.sandstone.SandShardRenderer;
import net.hazex.spiritsofthepast.entities.sandstone.SandstoneBoltRenderer;
import net.hazex.spiritsofthepast.entities.pharaoh.PharaohRenderer;
import net.hazex.spiritsofthepast.registries.SotPBlockEntityRegistry;
import net.hazex.spiritsofthepast.registries.SotPEntityRegistry;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = SpiritsofthePast.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = SpiritsofthePast.MODID, value = Dist.CLIENT)
public class SpiritsofthePastClient {
    public SpiritsofthePastClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SotPEntityRegistry.SAND_SHARD.get(), SandShardRenderer::new);
        event.registerEntityRenderer(SotPEntityRegistry.SANDFALL_EMITTER.get(), NoopRenderer::new);
        event.registerEntityRenderer(SotPEntityRegistry.SANDSTONE_BOLT.get(), SandstoneBoltRenderer::new);
        event.registerEntityRenderer(SotPEntityRegistry.PHARAOH.get(),
                context -> new PharaohRenderer<>(context, SotPEntityRegistry.PHARAOH.get()));

        event.registerEntityRenderer(SotPEntityRegistry.FOSSILIZED_JAVELIN.get(), FossilizedJavelinRenderer::new);

        event.registerBlockEntityRenderer(
                SotPBlockEntityRegistry.PHARAOHS_TOMB_BE.get(),
                context -> new GeoBlockRenderer<>(context, SotPBlockEntityRegistry.PHARAOHS_TOMB_BE.get()));

        event.registerBlockEntityRenderer(
                SotPBlockEntityRegistry.EMPTY_TOMB_BE.get(),
                context -> new GeoBlockRenderer<>(context, SotPBlockEntityRegistry.EMPTY_TOMB_BE.get()));
    }

    @SubscribeEvent
    private static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SandstoneBoltRenderer.LAYER, SandstoneBoltRenderer::createLayer);
        event.registerLayerDefinition(FossilizedJavelinRenderer.LAYER, FossilizedJavelinRenderer::createLayer);
    }
}
