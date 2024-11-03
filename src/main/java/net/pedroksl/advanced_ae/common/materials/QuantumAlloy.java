package net.pedroksl.advanced_ae.common.materials;

import java.util.EnumMap;
import java.util.List;

import net.minecraft.Util;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

public class QuantumAlloy {
    public static ArmorMaterial get() {
        return new ArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 4);
                    map.put(ArmorItem.Type.LEGGINGS, 6);
                    map.put(ArmorItem.Type.CHESTPLATE, 9);
                    map.put(ArmorItem.Type.HELMET, 4);
                    map.put(ArmorItem.Type.BODY, 14);
                }),
                15,
                SoundEvents.ARMOR_EQUIP_NETHERITE,
                () -> Ingredient.of(AAEItems.QUANTUM_ALLOY),
                List.of(new ArmorMaterial.Layer(AdvancedAE.makeId("quantum_alloy"), "", true)),
                3.0F,
                0.25F);
    }
}
