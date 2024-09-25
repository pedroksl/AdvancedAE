package net.pedroksl.advanced_ae.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import net.pedroksl.advanced_ae.network.packet.EnabledPatternsUpdatePacket;

import appeng.api.config.Settings;
import appeng.api.config.YesNo;
import appeng.api.util.IConfigManager;
import appeng.core.definitions.AEItems;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.RestrictedInputSlot;

public class QuantumCrafterMenu extends UpgradeableMenu<QuantumCrafterEntity> {

    @GuiSync(2)
    public YesNo meExport = YesNo.YES;

    public List<Boolean> enabledPatterns = new ArrayList<>();

    private static final String CONFIGURE_OUTPUT = "configureOutput";
    private static final String CONFIG_PATTERN = "configPattern";
    private static final String TOGGLE_ENABLE_PATTERN = "toggleEnablePattern";

    private final Slot[] patternSlots = new Slot[9];

    public QuantumCrafterMenu(int id, Inventory ip, QuantumCrafterEntity host) {
        super(AAEMenus.QUANTUM_CRAFTER, id, ip, host);

        var patterns = host.getPatternInventory();
        for (var x = 0; x < patterns.size(); x++) {
            this.patternSlots[x] = this.addSlot(
                    new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, patterns, x),
                    SlotSemantics.MACHINE_INPUT);
        }

        var outputs = host.getOutputInv();
        for (var x = 0; x < outputs.size(); x++) {
            this.addSlot(new AppEngSlot(outputs, x), SlotSemantics.MACHINE_OUTPUT);
        }

        setEnabledPatterns(host.getEnabledPatternSlots());

        registerClientAction(CONFIGURE_OUTPUT, this::configureOutput);
        registerClientAction(CONFIG_PATTERN, Integer.class, this::configPattern);
        registerClientAction(TOGGLE_ENABLE_PATTERN, Integer.class, this::toggleEnablePattern);
    }

    protected void loadSettingsFromHost(IConfigManager cm) {
        this.meExport = this.getHost().getConfigManager().getSetting(AAESettings.ME_EXPORT);
        this.setRedStoneMode(this.getHost().getConfigManager().getSetting(Settings.REDSTONE_CONTROLLED));
    }

    public YesNo getMeExport() {
        return meExport;
    }

    public void setEnabledPatterns(List<Boolean> enabledPatterns) {
        this.enabledPatterns = new ArrayList<>(enabledPatterns);

        broadcastChanges();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (isServerSide()) {
            sendPacketToClient(new EnabledPatternsUpdatePacket(this.enabledPatterns));
        }
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

    public void configPattern(int index) {
        if (isClientSide()) {
            sendClientAction(CONFIG_PATTERN, index);
            return;
        }

        var locator = getLocator();
        if (locator != null && isServerSide()) {

            var inputs = this.getHost().getPatternConfigInputs(index);
            var output = this.getHost().getPatternConfigOutput(index);

            if (inputs == null || output == null) return;

            QuantumCrafterConfigPatternMenu.open(
                    ((ServerPlayer) this.getPlayer()), getLocator(), index, inputs, output);
        }
    }

    public void toggleEnablePattern(int index) {
        if (isClientSide()) {
            sendClientAction(TOGGLE_ENABLE_PATTERN, index);
            return;
        }

        this.getHost().toggleEnablePattern(index);
        setEnabledPatterns(this.getHost().getEnabledPatternSlots());
    }

    @Override
    public boolean isValidForSlot(Slot s, ItemStack is) {
        for (var ps : this.patternSlots) {
            if (s == ps) {
                return AEItems.CRAFTING_PATTERN.is(is);
            }
        }

        return super.isValidForSlot(s, is);
    }
}
