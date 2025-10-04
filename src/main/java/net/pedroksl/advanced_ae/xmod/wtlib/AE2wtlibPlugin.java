package net.pedroksl.advanced_ae.xmod.wtlib;

import net.pedroksl.advanced_ae.common.helpers.QuantumCrafterWirelessTermMenuHost;
import net.pedroksl.advanced_ae.common.items.QuantumCrafterWirelessTerminalItem;
import net.pedroksl.advanced_ae.gui.QuantumCrafterWirelessTermMenu;

import de.mari_023.ae2wtlib.api.gui.Icon;
import de.mari_023.ae2wtlib.api.registration.AddTerminalEvent;

public class AE2wtlibPlugin {

    public static final QuantumCrafterWirelessTerminalItem TERMINAL = new QuantumCrafterWirelessTerminalItem();
    public static final String TERMINAL_ID = "quantum_crafter";

    static {
        AddTerminalEvent.register(e -> e.builder(
                        TERMINAL_ID,
                        QuantumCrafterWirelessTermMenuHost::new,
                        QuantumCrafterWirelessTermMenu.TYPE,
                        TERMINAL,
                        Icon.PATTERN_ACCESS)
                .addTerminal());
    }
}
