package net.pedroksl.advanced_ae.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEActionButton;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEActionItems;
import net.pedroksl.advanced_ae.client.gui.widgets.OutputDirectionButton;
import net.pedroksl.advanced_ae.gui.OutputDirectionMenu;

import appeng.api.orientation.RelativeSide;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.implementations.AESubScreen;
import appeng.client.gui.style.ScreenStyle;

public class OutputDirectionScreen extends AEBaseScreen<OutputDirectionMenu> {

    private static final int BUTTON_WIDTH = 18;
    private static final int BUTTON_HEIGHT = 18;

    private static final int BUTTON_TOP_OFFSET = 6;
    private static final int BUTTON_LEFT_OFFSET = 7;
    private static final int BUTTON_OFFSET = 2;

    private final List<OutputDirectionButton> buttons = new ArrayList<>();

    public OutputDirectionScreen(
            OutputDirectionMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        for (var side : RelativeSide.values()) {
            var pos = getButtonPosition(side);
            var button = new OutputDirectionButton(
                    this.leftPos + pos[0], this.topPos + pos[1], BUTTON_WIDTH, BUTTON_HEIGHT, this::buttonPressed);
            button.setSide(side);
            this.buttons.add(button);
            this.addRenderableWidget(button);
        }

        AESubScreen.addBackButton(menu, "back", widgets);

        AAEActionButton clearBtn = new AAEActionButton(AAEActionItems.CLEAR, btn -> menu.clearSides());
        clearBtn.setHalfSize(true);
        clearBtn.setDisableBackground(true);
        widgets.add("clearAll", clearBtn);
    }

    @Override
    protected void init() {
        super.init();

        for (var button : this.buttons) {
            var side = button.getSide();
            if (side != null) {
                var pos = getButtonPosition(button.getSide());
                button.setPosition(this.leftPos + pos[0], this.topPos + pos[1]);
            }
        }
    }

    public void update(Set<RelativeSide> sides) {
        for (var button : this.buttons) {
            var side = button.getSide();
            if (side != null) {
                button.setEnabled(sides.contains(side));
            }
        }
    }

    @Override
    protected void updateBeforeRender() {
        for (var button : this.buttons) {
            var side = button.getSide();
            if (side != null) {
                var pos = getButtonPosition(side);
                button.setPosition(this.leftPos + pos[0], this.topPos + pos[1]);

                ItemStack item = this.getMenu().getHost().getAdjacentBlock(side);
                button.setItemStack(item);
            }
        }

        super.updateBeforeRender();
    }

    private int[] getButtonPosition(RelativeSide side) {
        return switch (side) {
            case FRONT -> new int[] {
                BUTTON_LEFT_OFFSET + BUTTON_WIDTH + BUTTON_OFFSET, BUTTON_TOP_OFFSET + BUTTON_HEIGHT + BUTTON_OFFSET
            };
            case BACK -> new int[] {
                BUTTON_LEFT_OFFSET + 2 * BUTTON_WIDTH + 2 * BUTTON_OFFSET,
                BUTTON_TOP_OFFSET + 2 * BUTTON_HEIGHT + 2 * BUTTON_OFFSET
            };
            case TOP -> new int[] {BUTTON_LEFT_OFFSET + BUTTON_WIDTH + BUTTON_OFFSET, BUTTON_TOP_OFFSET};
            case RIGHT -> new int[] {BUTTON_LEFT_OFFSET, BUTTON_TOP_OFFSET + BUTTON_HEIGHT + BUTTON_OFFSET};
            case BOTTOM -> new int[] {
                BUTTON_LEFT_OFFSET + BUTTON_WIDTH + BUTTON_OFFSET,
                BUTTON_TOP_OFFSET + 2 * BUTTON_HEIGHT + 2 * BUTTON_OFFSET
            };
            case LEFT -> new int[] {
                BUTTON_LEFT_OFFSET + 2 * BUTTON_WIDTH + 2 * BUTTON_OFFSET,
                BUTTON_TOP_OFFSET + BUTTON_HEIGHT + BUTTON_OFFSET
            };
        };
    }

    private void buttonPressed(Button b) {
        if (b instanceof OutputDirectionButton button) {
            var side = button.getSide();
            if (side != null) {
                this.getMenu().updateSideStatus(button.getSide());
            }
        }
    }
}
