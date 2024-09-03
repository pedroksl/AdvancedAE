package net.pedroksl.advanced_ae.gui.reactionchamber;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;
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

    private final AppEngSlot tank;

    public ReactionChamberMenu(int id, Inventory ip, ReactionChamberEntity host) {
        super(AAEMenus.REACTION_CHAMBER, id, ip, host);

        var inputs = host.getInput();

        this.addSlot(new AppEngSlot(inputs, 0), SlotSemantics.MACHINE_INPUT);
        this.addSlot(new AppEngSlot(inputs, 1), SlotSemantics.MACHINE_INPUT);
        this.addSlot(new AppEngSlot(inputs, 2), SlotSemantics.MACHINE_INPUT);

        var output = new OutputSlot(host.getOutput(), 0, null);
        this.addSlot(output, SlotSemantics.MACHINE_OUTPUT);

        this.addSlot(this.tank = new AppEngSlot(new ConfigMenuInventory(host.getTank()), 0), SlotSemantics.STORAGE);
        this.tank.setEmptyTooltip(() -> List.of(
                Component.translatable("gui.extendedae.crystal_assembler.tank_empty"),
                Component.translatable("gui.extendedae.crystal_assembler.amount", new Object[] {0, 16000})
                        .withStyle(Tooltips.NORMAL_TOOLTIP_TEXT)));
    }

    protected void loadSettingsFromHost(IConfigManager cm) {
        this.autoExport = this.getHost().getConfigManager().getSetting(Settings.AUTO_EXPORT);
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
        return ReactionChamberRecipes.isValidIngredient(is, this.getHost().getLevel());
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
}
