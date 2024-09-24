package net.pedroksl.advanced_ae.gui;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.RestrictedInputSlot;

public class QuantumCrafterMenu extends UpgradeableMenu<QuantumCrafterEntity> {

    @GuiSync(2)
    public YesNo meExport = YesNo.YES;

    private static final String CONFIGURE_OUTPUT = "configureOutput";

    public QuantumCrafterMenu(int id, Inventory ip, QuantumCrafterEntity host) {
        super(AAEMenus.QUANTUM_CRAFTER, id, ip, host);

        var patterns = host.getPatternInventory();
        for (var x = 0; x < patterns.size(); x++) {
            this.addSlot(
                    new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, patterns, x),
                    SlotSemantics.MACHINE_INPUT);
        }

        var outputs = host.getOutputInv();
        for (var x = 0; x < outputs.size(); x++) {
            this.addSlot(new AppEngSlot(outputs, x), SlotSemantics.MACHINE_OUTPUT);
        }

        registerClientAction(CONFIGURE_OUTPUT, this::configureOutput);
    }

    protected void loadSettingsFromHost(IConfigManager cm) {
        this.meExport = this.getHost().getConfigManager().getSetting(AAESettings.ME_EXPORT);
        this.setRedStoneMode(this.getHost().getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED));
    }

    public YesNo getMeExport() {
        return meExport;
    }

    public void configureOutput() {
        if (isClientSide()) {
            sendClientAction(CONFIGURE_OUTPUT);
            return;
        }

        var locator = getLocator();
        if (locator != null && isServerSide()) {
            OutputDirectionMenu.open(
                    ((ServerPlayer) this.getPlayer()),
                    getLocator(),
                    this.getHost().getAllowedOutputs());
        }
    }
}
