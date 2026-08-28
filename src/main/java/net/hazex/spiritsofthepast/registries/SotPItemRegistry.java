package net.hazex.spiritsofthepast.registries;

import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.items.Armor.FossilArmor.FossilArmor;
import net.hazex.spiritsofthepast.items.Armor.MedjayArmor.MedjayArmor;
import net.hazex.spiritsofthepast.items.Utils.SotPJukeboxSongs;
import net.hazex.spiritsofthepast.items.Weapons.AnkhStaffItem;
import net.hazex.spiritsofthepast.items.Utils.SotPArmorMaterials;
import net.hazex.spiritsofthepast.items.Utils.SotPToolTiers;
import net.hazex.spiritsofthepast.items.Weapons.FossilizedJavelinItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SotPItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpiritsofthePast.MODID);


    /*
    *** Summon item
     */

    public static final DeferredItem<Item> SCARAB = ITEMS.registerItem(
            "scarab",
            Item::new);

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
                    6,
                    1.6f)
            ));

    public static final DeferredItem<Item> MEDJAY_SPEAR = ITEMS.registerItem("medjay_spear",
            properties -> new Item(properties.spear(SotPToolTiers.MEDJAY,
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


    public static final DeferredItem<Item> FOSSILIZED_JAVELIN = ITEMS.registerItem("fossilized_javelin",
            properties -> new FossilizedJavelinItem(properties.spear(SotPToolTiers.FOSSIL,
                    0.95f, 0.7f, 0.7f, 3.5f, 13f, 8.5f, 5.1f, 13.37f, 4.67f)
            ));

    /*
    *** Armor
     */

    public static final DeferredItem<Item> FOSSIL_HELMET = ITEMS.registerItem(
            "fossil_helmet",
            properties -> new FossilArmor(properties
                    .humanoidArmor(SotPArmorMaterials.FOSSIL_ARMOR_MATERIAL, ArmorType.HELMET)
            ));

    public static final DeferredItem<Item> FOSSIL_CHESTPLATE = ITEMS.registerItem(
            "fossil_chestplate",
            properties -> new FossilArmor(properties.humanoidArmor(SotPArmorMaterials.FOSSIL_ARMOR_MATERIAL, ArmorType.CHESTPLATE)
            ));
    public static final DeferredItem<Item> FOSSIL_LEGGINGS = ITEMS.registerItem(
            "fossil_leggings",
            properties -> new FossilArmor(properties.humanoidArmor(SotPArmorMaterials.FOSSIL_ARMOR_MATERIAL, ArmorType.LEGGINGS)
            ));
    public static final DeferredItem<Item> FOSSIL_BOOTS = ITEMS.registerItem(
            "fossil_boots",
            properties -> new FossilArmor(properties.humanoidArmor(SotPArmorMaterials.FOSSIL_ARMOR_MATERIAL, ArmorType.BOOTS)
            ));


    public static final DeferredItem<Item> MEDJAY_HELMET = ITEMS.registerItem(
            "medjay_helmet",
            properties -> new MedjayArmor(properties.humanoidArmor(SotPArmorMaterials.MEDJAY_ARMOR_MATERIAL, ArmorType.HELMET)));
    public static final DeferredItem<Item> MEDJAY_CHESTPLATE = ITEMS.registerItem(
            "medjay_chestplate",
            properties -> new MedjayArmor(properties.humanoidArmor(SotPArmorMaterials.MEDJAY_ARMOR_MATERIAL, ArmorType.CHESTPLATE)));
    public static final DeferredItem<Item> MEDJAY_LEGGINGS = ITEMS.registerItem(
            "medjay_leggings",
            properties -> new MedjayArmor(properties.humanoidArmor(SotPArmorMaterials.MEDJAY_ARMOR_MATERIAL, ArmorType.LEGGINGS)));
    public static final DeferredItem<Item> MEDJAY_BOOTS = ITEMS.registerItem(
            "medjay_boots",
            properties -> new MedjayArmor(properties.humanoidArmor(SotPArmorMaterials.MEDJAY_ARMOR_MATERIAL, ArmorType.BOOTS)));


    /*
    *** Misc
     */
    public static final DeferredItem<Item> DISTURBED_SANDS_MUSIC_DISC = ITEMS.registerItem("disturbed_sands_music_disc",
            properties -> new Item(properties
                    .jukeboxPlayable(SotPJukeboxSongs.DISTURBED_SANDS_KEY).stacksTo(1)
                    .rarity(Rarity.UNCOMMON)
            ));


    //Blocks
    public static final DeferredItem<BlockItem> PHARAOHS_TOMB_ITEM =
            ITEMS.registerSimpleBlockItem("pharaohs_tomb", SotPBlockRegistry.PHARAOHS_TOMB);

    public static final DeferredItem<BlockItem> CRUMBLING_SANDSTONE_BRICK_ITEM =
            ITEMS.registerSimpleBlockItem("crumbling_sandstone_brick", SotPBlockRegistry.CRUMBLING_SANDSTONE_BRICK);

    public static final DeferredItem<BlockItem> CRACKED_SANDSTONE_BRICK_ITEM =
            ITEMS.registerSimpleBlockItem("cracked_sandstone_brick", SotPBlockRegistry.CRACKED_SANDSTONE_BRICK);

    public static final DeferredItem<BlockItem> SANDSTONE_BRICK_ITEM =
            ITEMS.registerSimpleBlockItem("sandstone_brick", SotPBlockRegistry.SANDSTONE_BRICK);

    public static void register(IEventBus modEventBus){
        ITEMS.register(modEventBus);
    }
}
