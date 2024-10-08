package net.pedroksl.advanced_ae.common.items.armors;

import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class QuantumBoots extends QuantumArmorBase {
    public QuantumBoots(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.BOOTS, properties);

        this.possibleUpgrades.add(UpgradeType.STEP_ASSIST);
        this.possibleUpgrades.add(UpgradeType.JUMP_HEIGHT);
    }
}
