package net.hazex.spiritsofthepast.registries;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.items.Weapons.AnkhStaffItem;
import net.hazex.spiritsofthepast.items.Utils.SotPArmorMaterials;
import net.hazex.spiritsofthepast.items.Utils.SotPToolTiers;
import net.hazex.spiritsofthepast.items.Weapons.FossilizedJavelinItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SotPItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpiritsofthePast.MODID);

    /*
    *** Materials
     */

    public static final DeferredItem<Item> AMBER = ITEMS.registerItem(
            "amber",
            Item::new);

    public static final DeferredItem<Item> FOSSIL = ITEMS.registerItem(
            "fossil",
            Item::new);

    /*
    *** Weapons
     */

    public static final DeferredItem<Item> ANKH_STAFF = ITEMS.registerItem(
            "ankh_staff",
            properties -> new AnkhStaffItem(properties.stacksTo(1))
    );

    public static final DeferredItem<Item> AMBER_SPEAR = ITEMS.registerItem("amber_spear",
            properties -> new Item(properties.spear(SotPToolTiers.FOSSIL,
                    0.95f,
                    0.7f,
                    0.7f,
                    3.5f,
                    13f,
                    8.5f,
                    5.1f,
                    13.37f,
                    4.67f)
            ));

    public static final DeferredItem<Item> KHOPESH = ITEMS.registerItem("khopesh",
            properties -> new Item(properties.sword(SotPToolTiers.FOSSIL,
                    8,
                    1.6f)
            ));

    public static final DeferredItem<Item> FOSSILIZED_JAVELIN = ITEMS.registerItem("fossilized_javelin",
            properties -> new FossilizedJavelinItem(properties.spear(SotPToolTiers.FOSSIL,
                    0.95f, 0.7f, 0.7f, 3.5f, 13f, 8.5f, 5.1f, 13.37f, 4.67f)
            ));

    /*
    *** Armor
     */

    public static final DeferredItem<Item> FOSSIL_HELMET = ITEMS.registerItem(
            "fossil_helmet",
            properties -> new Item(properties.humanoidArmor(SotPArmorMaterials.FOSSIL_ARMOR_MATERIAL, ArmorType.HELMET)));
    public static final DeferredItem<Item> FOSSIL_CHESTPLATE = ITEMS.registerItem(
            "fossil_chestplate",
            properties -> new Item(properties.humanoidArmor(SotPArmorMaterials.FOSSIL_ARMOR_MATERIAL, ArmorType.CHESTPLATE)));
    public static final DeferredItem<Item> FOSSIL_LEGGINGS = ITEMS.registerItem(
            "fossil_leggings",
            properties -> new Item(properties.humanoidArmor(SotPArmorMaterials.FOSSIL_ARMOR_MATERIAL, ArmorType.LEGGINGS)));
    public static final DeferredItem<Item> FOSSIL_BOOTS = ITEMS.registerItem(
            "fossil_boots",
            properties -> new Item(properties.humanoidArmor(SotPArmorMaterials.FOSSIL_ARMOR_MATERIAL, ArmorType.BOOTS)));


    public static void register(IEventBus modEventBus){
        ITEMS.register(modEventBus);
    }
}
