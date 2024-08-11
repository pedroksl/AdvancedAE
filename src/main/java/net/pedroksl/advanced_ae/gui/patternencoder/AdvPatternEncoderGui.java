package net.pedroksl.advanced_ae.gui.patternencoder;

import appeng.api.stacks.AEKey;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.Scrollbar;
import appeng.core.AppEng;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.AdvancedAE;

import javax.annotation.Nullable;
import java.util.HashMap;

public class AdvPatternEncoderGui extends AEBaseScreen<AdvPatternEncoderContainer> {

	private static final int ROW_HEIGHT = 18;
	private static final int SLOT_SIZE = ROW_HEIGHT;
	private static final int VISIBLE_ROWS = 4;

	private static final int LIST_ANCHOR_X = 20;
	private static final int LIST_ANCHOR_Y = 35;

	private static final Rect2i SLOT_BBOX = new Rect2i(7, 121, SLOT_SIZE, SLOT_SIZE);


	private final Scrollbar scrollbar;
	private HashMap<AEKey, Direction> inputList = new HashMap<>();
	private final HashMap<Integer, DirectionInputButton[]> directionButtons = new HashMap<>();

	public AdvPatternEncoderGui(AdvPatternEncoderContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
		super(menu, playerInventory, title, style);
		this.scrollbar = widgets.addScrollBar("scrollbar", Scrollbar.SMALL);
	}

	@Override
	public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
		super.drawFG(guiGraphics, offsetX, offsetY, mouseX, mouseY);

		this.directionButtons.forEach((key, value) -> {
			for (int x = 0; x < 7; x++) {
				value[x].visible = true;
			}
		});
	}

	@Override
	public void drawBG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX,
	                   int mouseY, float partialTicks) {
		super.drawBG(guiGraphics, offsetX, offsetY, mouseX, mouseY, partialTicks);

		final int scrollLevel = scrollbar.getCurrentScroll();
		int currentX = offsetX + LIST_ANCHOR_X;
		int currentY = offsetY + LIST_ANCHOR_Y;

		int visibleRows = Math.min(VISIBLE_ROWS, this.inputList.size());
		for (int i = 0; i < visibleRows; ++i) {
			blit(guiGraphics, currentX, currentY, SLOT_BBOX);
			currentY += ROW_HEIGHT;
		}
	}

	@Override
	public void init() {
		super.init();
		this.refreshList();
	}

	public void update(HashMap<AEKey, Direction> inputList) {
		this.inputList.clear();
		this.directionButtons.clear();

		this.inputList = inputList;
		this.refreshList();
	}

	private void refreshList() {
		for (var key : this.inputList.keySet()) {
			//key.wrapForDisplayOrFilter();
			//Direction selectedDir = inputList.get(key);

			DirectionInputButton[] buttons = new DirectionInputButton[7];
			for (var x = 0; x < 7; x++) {
				var button = new DirectionInputButton(0, 0, 18, 18, getDirButtonTexture(x),
						this::directionButtonPressed);
				button.setKey(key);
				button.setIndex(x);
				button.visible = false;
				buttons[x] = button;
			}

			directionButtons.put(key.hashCode(), buttons);
		}

		this.resetScrollbar();
	}

	private void directionButtonPressed(Button b) {
		DirectionInputButton button = ((DirectionInputButton) b);
		this.inputList.put(button.getKey(), button.getDiretion());
	}

	private int getSelectedDirButton(@Nullable Direction dir) {
		if (dir == null) return 0;

		return switch (dir) {
			case NORTH -> 1;
			case EAST -> 2;
			case SOUTH -> 3;
			case WEST -> 4;
			case UP -> 5;
			case DOWN -> 6;
		};
	}

	private ResourceLocation getDirButtonTexture(int index) {
		return switch (index) {
			case 1 -> AdvancedAE.id("guis/north_button.png");
			case 2 -> AdvancedAE.id("guis/east_button.png");
			case 3 -> AdvancedAE.id("guis/south_button.png");
			case 4 -> AdvancedAE.id("guis/west_button.png");
			case 5 -> AdvancedAE.id("guis/up_button.png");
			case 6 -> AdvancedAE.id("guis/down_button.png");
			default -> AdvancedAE.id("guis/any_button.png");
		};
	}

	private void resetScrollbar() {
		// Needs to take the border into account, so offset for 1 px on the top and bottom.
		scrollbar.setHeight(VISIBLE_ROWS * ROW_HEIGHT - 2);
		scrollbar.setRange(0, this.inputList.size() - VISIBLE_ROWS, 2);
	}

	private void blit(GuiGraphics guiGraphics, int offsetX, int offsetY, Rect2i srcRect) {
		var texture = AppEng.makeId("textures/guis/adv_pattern_encoder.png");
		guiGraphics.blit(texture, offsetX, offsetY, srcRect.getX(), srcRect.getY(), srcRect.getWidth(),
				srcRect.getHeight());
	}
}
