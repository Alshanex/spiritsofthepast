package net.hazex.spiritsofthepast;

import net.hazex.spiritsofthepast.entities.pharaoh.PharaohEntity;
import net.hazex.spiritsofthepast.registries.*;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SpiritsofthePast.MODID)
public class SpiritsofthePast {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "spiritsofthepast";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SpiritsofthePast(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);

        SotPEntityRegistry.register(modEventBus);

        SotPItemRegistry.register(modEventBus);
        SotPCreativeModeTabs.register(modEventBus);
        SotPEffectRegistry.register(modEventBus);
        SotPBlockRegistry.register(modEventBus);
        SotPBlockEntityRegistry.register(modEventBus);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        modEventBus.addListener(this::registerAttributes);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, SotPConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(SotPItemRegistry.ANKH_STAFF);
            event.accept(SotPItemRegistry.AMBER_SPEAR);
            event.accept(SotPItemRegistry.KHOPESH);

            event.accept(SotPItemRegistry.FOSSIL_HELMET);
            event.accept(SotPItemRegistry.FOSSIL_CHESTPLATE);
            event.accept(SotPItemRegistry.FOSSIL_LEGGINGS);
            event.accept(SotPItemRegistry.FOSSIL_BOOTS);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(SotPItemRegistry.AMBER);
            event.accept(SotPItemRegistry.FOSSIL);
        }
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(SotPEntityRegistry.PHARAOH.get(), PharaohEntity.createAttributes().build());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}
