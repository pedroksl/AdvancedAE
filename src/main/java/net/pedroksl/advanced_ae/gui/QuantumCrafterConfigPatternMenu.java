package net.pedroksl.advanced_ae.gui;

import java.util.LinkedHashMap;

import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.helpers.AutoCraftingContainer;
import net.pedroksl.advanced_ae.network.packet.PatternConfigServerUpdatePacket;

import appeng.api.stacks.AEKey;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;

public class QuantumCrafterConfigPatternMenu extends AEBaseMenu implements ISubMenu {

    private int index;
    private final ISubMenuHost host;
    private AutoCraftingContainer crafter;

    private final String SET_MAX_CRAFTED = "set_max_crafted";

    public LinkedHashMap<AEKey, Long> inputs = new LinkedHashMap<>();
    public Pair<AEKey, Long> output;

    public QuantumCrafterConfigPatternMenu(int id, Inventory ip, ISubMenuHost host) {
        this(AAEMenus.CRAFTER_PATTERN_CONFIG.get(), id, ip, host);
    }

    protected QuantumCrafterConfigPatternMenu(
            MenuType<? extends QuantumCrafterConfigPatternMenu> type, int id, Inventory ip, ISubMenuHost host) {
        super(type, id, ip, host);
        this.host = host;

        registerClientAction(SET_MAX_CRAFTED, Long.class, this::setMaxCrafted);
    }

    @Override
    public ISubMenuHost getHost() {
        return host;
    }

    public static void open(
            ServerPlayer player,
            MenuHostLocator locator,
            AutoCraftingContainer crafter,
            int index,
            LinkedHashMap<AEKey, Long> inputs,
            Pair<AEKey, Long> output) {
        MenuOpener.open(AAEMenus.CRAFTER_PATTERN_CONFIG.get(), player, locator);

        if (player.containerMenu instanceof QuantumCrafterConfigPatternMenu cca) {
            cca.setCrafter(crafter);
            cca.setIndex(index);
            cca.setInputsAndOutput(inputs, output);
            cca.broadcastChanges();
        }
    }

    private void setCrafter(AutoCraftingContainer crafter) {
        this.crafter = crafter;
    }

    private void setIndex(int index) {
        this.index = index;
    }

    private void setInputsAndOutput(LinkedHashMap<AEKey, Long> inputs, Pair<AEKey, Long> output) {
        this.inputs = new LinkedHashMap<>(inputs);
        this.output = new Pair<>(output.getFirst(), output.getSecond());

        if (isServerSide() && this.inputs != null && this.output != null) {
            sendPacketToClient(new PatternConfigServerUpdatePacket(this.inputs, this.output));
        }
    }

    public Level getLevel() {
        return this.getPlayerInventory().player.level();
    }

    public void setStockAmount(int inputIndex, long amount) {
        crafter.setStockAmount(this.index, inputIndex, amount);
        setInputsAndOutput(crafter.getPatternConfigInputs(this.index), crafter.getPatternConfigOutput(this.index));
    }

    public void setMaxCrafted(long amount) {
        if (isClientSide()) {
            sendClientAction(SET_MAX_CRAFTED, amount);
            return;
        }

        crafter.setMaxCrafted(this.index, amount);
        setInputsAndOutput(crafter.getPatternConfigInputs(this.index), crafter.getPatternConfigOutput(this.index));
    }
}
