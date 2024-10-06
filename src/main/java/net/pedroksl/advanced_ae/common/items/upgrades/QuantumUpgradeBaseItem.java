package net.pedroksl.advanced_ae.common.items.upgrades;

import net.minecraft.world.item.Item;

public class QuantumUpgradeBaseItem extends Item {
	private final UpgradeType type;

	public QuantumUpgradeBaseItem(Properties properties) {
		super(properties);
		this.type = UpgradeType.EMPTY;
	}

	protected QuantumUpgradeBaseItem(UpgradeType type, Properties properties) {
		super(properties);
		this.type = type;
	}

	public UpgradeType getType() {
		return type;
	}
}
