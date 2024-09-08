package net.pedroksl.advanced_ae.gui.reactionchamber;

import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;
import net.pedroksl.advanced_ae.gui.config.OutputDirectionMenu;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipes;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.core.localization.Tooltips;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.OutputSlot;
import appeng.util.ConfigMenuInventory;

public class ReactionChamberMenu extends UpgradeableMenu<ReactionChamberEntity> implements IProgressProvider {

    @GuiSync(2)
    public int maxProcessingTime = -1;

    @GuiSync(3)
    public int processingTime = -1;

    @GuiSync(7)
    public YesNo autoExport = YesNo.NO;

    private static final String FLUSH_FLUID = "flushFluid";

    private final Slot first;
    private final Slot second;
    private final Slot third;
    private final AppEngSlot tank;

    private boolean initialized = false;

    public ReactionChamberMenu(int id, Inventory ip, ReactionChamberEntity host) {
        super(AAEMenus.REACTION_CHAMBER, id, ip, host);

        var inputs = host.getInput();

        this.first = this.addSlot(new AppEngSlot(inputs, 0), SlotSemantics.MACHINE_INPUT);
        this.second = this.addSlot(new AppEngSlot(inputs, 1), SlotSemantics.MACHINE_INPUT);
        this.third = this.addSlot(new AppEngSlot(inputs, 2), SlotSemantics.MACHINE_INPUT);

        var output = new OutputSlot(host.getOutput(), 0, null);
        this.addSlot(output, SlotSemantics.MACHINE_OUTPUT);

        this.addSlot(this.tank = new AppEngSlot(new ConfigMenuInventory(host.getTank()), 0), SlotSemantics.STORAGE);
        this.tank.setEmptyTooltip(() -> List.of(
                AAEText.TankEmpty.text(), AAEText.TankAmount.text(0, 16000).withStyle(Tooltips.NORMAL_TOOLTIP_TEXT)));

        registerClientAction(FLUSH_FLUID, this::clearFluid);
    }

    protected void loadSettingsFromHost(IConfigManager cm) {
        var autoExport = this.getHost().getConfigManager().getSetting(Settings.AUTO_EXPORT);
        if (autoExport != this.autoExport && autoExport == YesNo.YES && initialized) {
            var locator = getLocator();
            if (locator != null && isServerSide()) {
                OutputDirectionMenu.open(
                        ((ServerPlayer) this.getPlayer()),
                        getLocator(),
                        this.getHost().getAllowedOutputs());
            }
        }
        this.autoExport = autoExport;
        if (!initialized) {
            initialized = true;
        }
    }

    @Override
    protected void standardDetectAndSendChanges() {
        if (isServerSide()) {
            this.maxProcessingTime = getHost().getMaxProcessingTime();
            this.processingTime = getHost().getProcessingTime();
        }
        super.standardDetectAndSendChanges();
    }

    @Override
    public boolean isValidForSlot(Slot s, ItemStack is) {
        if (s == this.first || s == this.second || s == this.third) {
            return ReactionChamberRecipes.isValidIngredient(is, this.getHost().getLevel());
        } else if (s == this.tank) {
            System.out.println("tank input found");
        }
        return true;
    }

    @Override
    public int getCurrentProgress() {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress() {
        return this.maxProcessingTime;
    }

    public YesNo getAutoExport() {
        return autoExport;
    }

    public void clearFluid() {
        if (isClientSide()) {
            sendClientAction(FLUSH_FLUID);
            return;
        }

        this.getHost().clearFluid();
    }
}
