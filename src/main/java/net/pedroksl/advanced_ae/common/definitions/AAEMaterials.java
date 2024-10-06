package net.pedroksl.advanced_ae.common.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorMaterial;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.materials.QuantumAlloy;

public class AAEMaterials {
    public static final DeferredRegister<ArmorMaterial> DR =
            DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, AdvancedAE.MOD_ID);

    private static final List<MaterialDefinition<?>> MATERIALS = new ArrayList<>();

    public static List<MaterialDefinition<?>> getMaterials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    public static final MaterialDefinition<ArmorMaterial> QUANTUM_ALLOY =
            material("Quantum Alloy", "quantum_alloy", QuantumAlloy::get);

    private static MaterialDefinition<ArmorMaterial> material(
            String englishName, String id, Supplier<ArmorMaterial> material) {
        var definition = new MaterialDefinition<>(englishName, DR.register(id, material));
        MATERIALS.add(definition);
        return definition;
    }
}
