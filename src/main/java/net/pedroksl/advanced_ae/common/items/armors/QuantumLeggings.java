package net.pedroksl.advanced_ae.common.items.armors;

import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class QuantumLeggings extends QuantumArmorBase {
    public QuantumLeggings(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.LEGGINGS, properties);

        this.possibleUpgrades.add(UpgradeType.WALK_SPEED);
        this.possibleUpgrades.add(UpgradeType.SPRINT_SPEED);
    }
}
