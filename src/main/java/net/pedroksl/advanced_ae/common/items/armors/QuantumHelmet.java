package net.pedroksl.advanced_ae.common.items.armors;

import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import java.util.List;

public class QuantumHelmet extends QuantumArmorBase {

    private final List<UpgradeType> possibleUpgrades;

    public QuantumHelmet(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.HELMET, properties);

        this.possibleUpgrades = List.of();
    }

    @Override
    public boolean isUpgradeAllowed(UpgradeType type) {
        return possibleUpgrades.contains(type);
    }
}
