package net.pedroksl.advanced_ae.gui.patternencoder;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class AdvPatternEncoderGui extends AEBaseScreen<AdvPatternEncoderContainer> {
	public AdvPatternEncoderGui(AdvPatternEncoderContainer menu, Inventory playerInventory, Component title, ScreenStyle style) {
		super(menu, playerInventory, title, style);
	}

//	@Override
//	public void drawFG(GuiGraphics guiGraphics, int offsetX, int offsetY, int mouseX, int mouseY) {
//		guiGraphics.drawString(
//				this.font,
//				Component.translatable("gui.expatternprovider.pattern_modifier", this.getModeName()),
//				8,
//				6,
//				style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB(),
//				false
//		);
//		if (this.menu.page == 2) {
//			guiGraphics.drawString(
//					this.font,
//					Component.translatable("gui.expatternprovider.pattern_modifier.blank"),
//					52,
//					57,
//					style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB(),
//					false
//			);
//			guiGraphics.drawString(
//					this.font,
//					Component.translatable("gui.expatternprovider.pattern_modifier.target"),
//					52,
//					25,
//					style.getColor(PaletteColor.DEFAULT_TEXT_COLOR).toARGB(),
//					false
//			);
//		}
//	}
}
