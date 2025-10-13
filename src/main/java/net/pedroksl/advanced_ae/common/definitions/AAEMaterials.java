package net.pedroksl.advanced_ae.common.definitions;

import net.minecraft.world.item.ArmorMaterial;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.materials.QuantumAlloy;
import net.pedroksl.ae2addonlib.registry.MaterialRegistry;
import net.pedroksl.ae2addonlib.registry.helpers.MaterialDefinition;

public class AAEMaterials extends MaterialRegistry {

    public static final AAEMaterials INSTANCE = new AAEMaterials();

    AAEMaterials() {
        super(AdvancedAE.MOD_ID);
    }

    public static final MaterialDefinition<ArmorMaterial> QUANTUM_ALLOY =
            material(AdvancedAE.MOD_ID, "Quantum Alloy", "quantum_alloy", QuantumAlloy::get);
}
