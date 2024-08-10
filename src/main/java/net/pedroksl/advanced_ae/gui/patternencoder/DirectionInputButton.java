package net.pedroksl.advanced_ae.gui.patternencoder;

import appeng.api.stacks.AEKey;
import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import net.minecraft.core.Direction;

public class DirectionInputButton extends IconButton {

	private Icon icon;
	private AEKey key;
	private int index;

	public DirectionInputButton(OnPress onPress) {
		super(onPress);
	}

	public void setKey(AEKey key) {
		this.key = key;
	}

	public AEKey getKey() {
		return this.key;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Direction getDiretion() {
		return switch (index) {
			case 1 -> Direction.NORTH;
			case 2 -> Direction.EAST;
			case 3 -> Direction.SOUTH;
			case 4 -> Direction.WEST;
			case 5 -> Direction.UP;
			case 6 -> Direction.DOWN;
			default -> null;
		};
	}

	@Override
	protected Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}
}
