package net.hazex.spiritsofthepast.registries;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.items.AnkhStaffItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpiritsofthePast.MODID);

    public static final DeferredItem<Item> ANKH_STAFF = ITEMS.registerItem(
            "ankh_staff",
            properties -> new AnkhStaffItem(properties.stacksTo(1))
    );

    public static void register(IEventBus modEventBus){
        ITEMS.register(modEventBus);
    }
}
