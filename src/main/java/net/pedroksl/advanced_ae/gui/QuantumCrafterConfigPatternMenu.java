package net.pedroksl.advanced_ae.gui;

import java.util.HashMap;

import com.mojang.datafixers.util.Pair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import net.pedroksl.advanced_ae.network.packet.PatternConfigServerUpdatePacket;

import appeng.api.stacks.AEKey;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuHostLocator;

public class QuantumCrafterConfigPatternMenu extends AEBaseMenu implements ISubMenu {

    private int index;
    private final QuantumCrafterEntity host;

    private final String SET_MAX_CRAFTED = "set_max_crafted";

    public HashMap<AEKey, Long> inputs = new HashMap<>();
    public Pair<AEKey, Long> output;

    public QuantumCrafterConfigPatternMenu(int id, Inventory ip, QuantumCrafterEntity host) {
        this(AAEMenus.CRAFTER_PATTERN_CONFIG, id, ip, host);
    }

    protected QuantumCrafterConfigPatternMenu(
            MenuType<? extends QuantumCrafterConfigPatternMenu> type, int id, Inventory ip, QuantumCrafterEntity host) {
        super(type, id, ip, host);
        this.host = host;

        registerClientAction(SET_MAX_CRAFTED, Long.class, this::setMaxCrafted);
    }

    @Override
    public QuantumCrafterEntity getHost() {
        return host;
    }

    public static void open(
            ServerPlayer player,
            MenuHostLocator locator,
            int index,
            HashMap<AEKey, Long> inputs,
            Pair<AEKey, Long> output) {
        MenuOpener.open(AAEMenus.CRAFTER_PATTERN_CONFIG, player, locator);

        if (player.containerMenu instanceof QuantumCrafterConfigPatternMenu cca) {
            cca.setIndex(index);
            cca.setInputsAndOutput(inputs, output);
            cca.broadcastChanges();
        }
    }

    private void setIndex(int index) {
        this.index = index;
    }

    private void setInputsAndOutput(HashMap<AEKey, Long> inputs, Pair<AEKey, Long> output) {
        this.inputs = new HashMap<>(inputs);
        this.output = new Pair<>(output.getFirst(), output.getSecond());

        if (isServerSide() && this.inputs != null && this.output != null) {
            sendPacketToClient(new PatternConfigServerUpdatePacket(this.inputs, this.output));
        }
    }

    public Level getLevel() {
        return this.getPlayerInventory().player.level();
    }

    public void setStockAmount(int inputIndex, long amount) {
        getHost().setStockAmount(this.index, inputIndex, amount);
        setInputsAndOutput(
                getHost().getPatternConfigInputs(this.index), getHost().getPatternConfigOutput(this.index));
    }

    public void setMaxCrafted(long amount) {
        if (isClientSide()) {
            sendClientAction(SET_MAX_CRAFTED, amount);
            return;
        }

        getHost().setMaxCrafted(this.index, amount);
        setInputsAndOutput(
                getHost().getPatternConfigInputs(this.index), getHost().getPatternConfigOutput(this.index));
    }
}
