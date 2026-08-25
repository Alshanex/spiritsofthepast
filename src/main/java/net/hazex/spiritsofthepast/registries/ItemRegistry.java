package net.hazex.spiritsofthepast.registries;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.items.SandfallStaffItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpiritsofthePast.MODID);

    public static final DeferredItem<Item> SANDFALL_STAFF = ITEMS.registerItem(
            "sandfall_staff",
            properties -> new SandfallStaffItem(properties.stacksTo(1))
    );

    public static void register(IEventBus modEventBus){
        ITEMS.register(modEventBus);
    }
}
