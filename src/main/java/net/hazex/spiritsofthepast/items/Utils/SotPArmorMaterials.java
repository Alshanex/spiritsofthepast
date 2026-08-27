package net.hazex.spiritsofthepast.items.Utils;

import com.google.common.collect.Maps;
import net.hazex.spiritsofthepast.SpiritsofthePast;
import net.hazex.spiritsofthepast.datagen.SotPTags;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.Map;

public class SotPArmorMaterials {
    private static ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID =
            ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));

    public static ResourceKey<EquipmentAsset> FOSSIL_KEY = ResourceKey.create(ROOT_ID,
            Identifier.fromNamespaceAndPath(SpiritsofthePast.MODID, "fossil"));

    public static final ArmorMaterial FOSSIL_ARMOR_MATERIAL = new ArmorMaterial(29,
            makeDefense(3, 6, 8, 3, 12), 18, SoundEvents.ARMOR_EQUIP_IRON,
            1f,
            0f,
            SotPTags.Items.FOSSIL_REPAIR, FOSSIL_KEY);


    private static Map<ArmorType, Integer> makeDefense(int boots, int legs, int chest, int helm, int body) {
        return Maps.newEnumMap(
                Map.of(ArmorType.BOOTS, boots, ArmorType.LEGGINGS, legs, ArmorType.CHESTPLATE, chest, ArmorType.HELMET, helm, ArmorType.BODY, body)
        );
    }
}