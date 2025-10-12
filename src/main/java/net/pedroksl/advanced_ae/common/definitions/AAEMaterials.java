package net.pedroksl.advanced_ae.common.definitions;

import net.minecraft.world.item.ArmorMaterial;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.materials.QuantumAlloy;
import net.pedroksl.ae2addonlib.registry.AddonMaterials;
import net.pedroksl.ae2addonlib.registry.helpers.MaterialDefinition;

public class AAEMaterials extends AddonMaterials {

    public static final AAEMaterials INSTANCE = new AAEMaterials();

    AAEMaterials() {
        super(AdvancedAE.MOD_ID);
    }

    public static final MaterialDefinition<ArmorMaterial> QUANTUM_ALLOY =
            material("Quantum Alloy", "quantum_alloy", QuantumAlloy::get);
}
