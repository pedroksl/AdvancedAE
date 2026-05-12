package net.pedroksl.advanced_ae.common.materials;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAssets;

public interface AAEMaterials {
    ArmorMaterial QUANTUM_ALLOY = new ArmorMaterial(
            37,
            Maps.newEnumMap(Map.of(
                    ArmorType.BOOTS,
                    4,
                    ArmorType.LEGGINGS,
                    7,
                    ArmorType.CHESTPLATE,
                    9,
                    ArmorType.HELMET,
                    4,
                    ArmorType.BODY,
                    15)),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            5.0F,
            0.25F,
            null,
            EquipmentAssets.createId("quantum_alloy"));
}
