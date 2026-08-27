package net.hazex.spiritsofthepast.registries;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SotPCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpiritsofthePast.MODID);

    public static final Supplier<CreativeModeTab> SOTP_ITEMS = CREATIVE_MODE_TABS.register("spiritsofthepast_items",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(SotPItemRegistry.FOSSIL_HELMET.get()))
                    .title(Component.translatable("creativetab.spiritsofthepast.items"))
                    .withTabsBefore(CreativeModeTabs.INGREDIENTS)
                    //.withTabsAfter(Identifier.fromNamespaceAndPath(SpiritsofthePast.MOD_ID, "spiritsofthepast_blocks"))
                    .displayItems((itemDisplayParameters, output) -> {

                        // Weapons
                        output.accept(SotPItemRegistry.AMBER_SPEAR);
                        output.accept(SotPItemRegistry.ANKH_STAFF);
                        output.accept(SotPItemRegistry.KHOPESH);
                        output.accept(SotPItemRegistry.FOSSILIZED_JAVELIN);

                        // Armor
                        output.accept(SotPItemRegistry.FOSSIL_HELMET);
                        output.accept(SotPItemRegistry.FOSSIL_CHESTPLATE);
                        output.accept(SotPItemRegistry.FOSSIL_LEGGINGS);
                        output.accept(SotPItemRegistry.FOSSIL_BOOTS);

                        // Materials
                        output.accept(SotPItemRegistry.AMBER);
                        output.accept(SotPItemRegistry.FOSSIL);

                        //Blocks
                        output.accept(SotPItemRegistry.PHARAOHS_TOMB_ITEM);


                    }).build());

    /*
    public static final Supplier<CreativeModeTab> SOTP_BLOCKS = CREATIVE_MODE_TABS.register("spiritsofthepast_blocks",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(SotPBlocks.SANDSTONE_BRICK.get()))
                    .title(Component.translatable("creativetab.spiritsofthepast.blocks"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(SotPBlocks.SANDSTONE_BRICK);


                    }).build());
    */


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}