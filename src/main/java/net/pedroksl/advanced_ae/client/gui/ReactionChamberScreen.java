package net.pedroksl.advanced_ae.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEActionButton;
import net.pedroksl.advanced_ae.client.gui.widgets.AAEActionItems;
import net.pedroksl.advanced_ae.gui.ReactionChamberMenu;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.*;

public class ReactionChamberScreen extends UpgradeableScreen<ReactionChamberMenu> {

    private final ProgressBar pb;
    private final SettingToggleButton<YesNo> autoExportBtn;

    public ReactionChamberScreen(
            ReactionChamberMenu menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);

        this.pb = new ProgressBar(this.menu, style.getImage("progressBar"), ProgressBar.Direction.VERTICAL);
        widgets.add("progressBar", this.pb);

        this.autoExportBtn = new ServerSettingToggleButton<>(Settings.AUTO_EXPORT, YesNo.NO);
        this.addToLeftToolbar(autoExportBtn);

        AAEActionButton clearBtn = new AAEActionButton(AAEActionItems.F_FLUSH, btn -> menu.clearFluid());
        clearBtn.setHalfSize(true);
        clearBtn.setDisableBackground(true);
        widgets.add("clearFluid", clearBtn);
    }

    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();

        int progress = this.menu.getCurrentProgress() * 100 / this.menu.getMaxProgress();
        this.pb.setFullMsg(Component.literal(progress + "%"));

        this.autoExportBtn.set(getMenu().getAutoExport());
    }
}
