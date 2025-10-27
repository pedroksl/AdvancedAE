package net.pedroksl.advanced_ae.common.parts;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.api.AAESettings;
import net.pedroksl.advanced_ae.api.IQuantumCrafterTermMenuHost;
import net.pedroksl.advanced_ae.api.ShowQuantumCrafters;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.IConfigManager;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.reporting.AbstractDisplayPart;
import appeng.util.ConfigManager;

public class QuantumCrafterTerminalPart extends AbstractDisplayPart implements IQuantumCrafterTermMenuHost {

    @PartModels
    public static final ResourceLocation MODEL_OFF = AdvancedAE.makeId("part/quantum_crafter_terminal_off");

    @PartModels
    public static final ResourceLocation MODEL_ON = AdvancedAE.makeId("part/quantum_crafter_terminal_on");

    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODEL_OFF, MODEL_STATUS_OFF);
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_ON);
    public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODEL_ON, MODEL_STATUS_HAS_CHANNEL);

    private final IConfigManager configManager;

    public QuantumCrafterTerminalPart(IPartItem<?> partItem) {
        super(partItem, true);

        configManager = new ConfigManager(() -> this.getHost().markForSave());
        configManager.registerSetting(AAESettings.TERMINAL_SHOW_QUANTUM_CRAFTERS, ShowQuantumCrafters.VISIBLE);
    }

    @Override
    public boolean onPartActivate(Player player, InteractionHand hand, Vec3 pos) {
        if (!super.onPartActivate(player, hand, pos) && !isClientSide()) {
            MenuOpener.open(AAEMenus.QUANTUM_CRAFTER_TERMINAL.get(), player, MenuLocators.forPart(this));
        }
        return true;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.configManager;
    }

    public void writeToNBT(CompoundTag tag) {
        super.writeToNBT(tag);
        configManager.writeToNBT(tag);
    }

    public void readFromNBT(CompoundTag tag) {
        super.readFromNBT(tag);
        configManager.readFromNBT(tag);
    }

    @Override
    public IPartModel getStaticModels() {
        return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.open(AAEMenus.QUANTUM_CRAFTER_TERMINAL.get(), player, subMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return AAEItems.QUANTUM_CRAFTER_TERMINAL.stack();
    }
}
