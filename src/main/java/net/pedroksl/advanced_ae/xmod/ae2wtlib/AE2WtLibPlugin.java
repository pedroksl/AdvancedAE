package net.pedroksl.advanced_ae.xmod.ae2wtlib;

import net.minecraftforge.registries.ForgeRegistries;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.gui.QuantumCrafterWirelessTermScreen;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.helpers.QuantumCrafterWirelessTermMenuHost;
import net.pedroksl.advanced_ae.gui.QuantumCrafterWirelessTermMenu;

import appeng.api.features.GridLinkables;
import appeng.api.upgrades.Upgrades;
import appeng.init.client.InitScreens;

import de.mari_023.ae2wtlib.AE2wtlib;
import de.mari_023.ae2wtlib.terminal.IUniversalWirelessTerminalItem;
import de.mari_023.ae2wtlib.wut.WUTHandler;

public class AE2WtLibPlugin {

    public static final String TERMINAL_ID = "quantum_crafter";

    public static void commonInit() {
        if (AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL != null) {
            WUTHandler.addTerminal(
                    TERMINAL_ID,
                    ((IUniversalWirelessTerminalItem) AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL.get())::tryOpen,
                    QuantumCrafterWirelessTermMenuHost::new,
                    QuantumCrafterWirelessTermMenu.TYPE,
                    (IUniversalWirelessTerminalItem) AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL.get(),
                    AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL.asItem().getDescriptionId());
        }
    }

    public static void initUpgrades() {
        if (AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL != null) {
            Upgrades.add(
                    AE2wtlib.QUANTUM_BRIDGE_CARD,
                    AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL,
                    1,
                    AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL.asItem().getDescriptionId());
        }
    }

    public static void initGridLinkables() {
        if (AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL != null) {
            GridLinkables.register(
                    AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL,
                    appeng.items.tools.powered.WirelessTerminalItem.LINKABLE_HANDLER);
        }
    }

    public static void initMenu() {
        ForgeRegistries.MENU_TYPES.register(
                AdvancedAE.makeId("wireless_quantum_crafter_terminal"), QuantumCrafterWirelessTermMenu.TYPE);
    }

    public static void initScreen() {
        InitScreens.<QuantumCrafterWirelessTermMenu, QuantumCrafterWirelessTermScreen>register(
                QuantumCrafterWirelessTermMenu.TYPE,
                QuantumCrafterWirelessTermScreen::new,
                "/screens/wireless_quantum_crafter_terminal.json");
    }
}
