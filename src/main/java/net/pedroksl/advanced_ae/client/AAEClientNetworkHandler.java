package net.pedroksl.advanced_ae.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.pedroksl.advanced_ae.client.gui.*;
import net.pedroksl.advanced_ae.network.packet.*;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.QuantumArmorUpgradeStatePacket;
import net.pedroksl.ae2addonlib.client.ClientNetworkHandler;

public class AAEClientNetworkHandler extends ClientNetworkHandler {

    @Override
    public void registerPackets(RegisterClientPayloadHandlersEvent event) {
        register(event, AdvPatternEncoderPacket.TYPE, this::handleAdvPatternEncoderPacket);
        register(event, PatternConfigServerUpdatePacket.TYPE, this::handlePatternConfigServerUpdatePacket);
        register(event, PatternsUpdatePacket.TYPE, this::handlePatternsUpdatePacket);
        register(event, QuantumArmorUpgradeStatePacket.TYPE, this::handleQuantumArmorUpgradeStatePacket);
        register(event, MenuSelectionPacket.TYPE, this::handleMenuSelectionPacket);
        register(event, ItemTrackingPacket.TYPE, this::handleItemTrackingPacket);
        register(event, QuantumCrafterTerminalPacket.TYPE, this::handleQuantumCrafterTerminalPacket);
        register(event, ClearQuantumCrafterTerminalPacket.TYPE, this::handleClearQuantumCrafterTerminalPacket);
    }

    public void handleAdvPatternEncoderPacket(AdvPatternEncoderPacket packet, Minecraft minecraft, Player player) {
        if (Minecraft.getInstance().screen instanceof AdvPatternEncoderScreen encoderGui) {
            encoderGui.update(packet.dirMap());
        }
    }

    public void handlePatternConfigServerUpdatePacket(
            PatternConfigServerUpdatePacket packet, Minecraft minecraft, Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterConfigPatternScreen screen) {
            screen.update(packet.inputs(), packet.output());
        }
    }

    public void handlePatternsUpdatePacket(PatternsUpdatePacket packet, Minecraft minecraft, Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterScreen screen) {
            screen.updateInvalidButtons(packet.invalidPatterns());
            screen.updateEnabledButtons(packet.enabledPatterns());
        }
    }

    public void handleQuantumArmorUpgradeStatePacket(
            QuantumArmorUpgradeStatePacket packet, Minecraft minecraft, Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumArmorConfigScreen screen) {
            screen.refreshList(packet.selectedIndex(), packet.states());
        }
    }

    public void handleMenuSelectionPacket(MenuSelectionPacket packet, Minecraft minecraft, Player player) {
        if (packet.menuType() == -1) {
            player.getPersistentData().remove(packet.data());
        } else {
            player.getPersistentData().putInt(packet.data(), packet.menuType());
        }
    }

    public void handleItemTrackingPacket(ItemTrackingPacket packet, Minecraft minecraft, Player player) {
        if (player != null) {
            Entity entity = player.level().getEntity(packet.entityId());
            if (entity instanceof ItemEntity e) {
                e.thrower = EntityReference.of(packet.thrower());
                e.setPickUpDelay(packet.pickupDelay());
            }
        }
    }

    public void handleQuantumCrafterTerminalPacket(
            QuantumCrafterTerminalPacket packet, Minecraft minecraft, Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterTermScreen<?> screen) {
            if (packet.fullUpdate()) {
                screen.postFullUpdate(
                        packet.inventoryId(),
                        packet.sortBy(),
                        packet.inventorySize(),
                        packet.slots(),
                        packet.enabledArray(),
                        packet.invalidArray());
            } else {
                screen.postIncrementalUpdate(
                        packet.inventoryId(), packet.slots(), packet.enabledArray(), packet.invalidArray());
            }
        }
    }

    public void handleClearQuantumCrafterTerminalPacket(
            ClearQuantumCrafterTerminalPacket packet, Minecraft minecraft, Player player) {
        if (Minecraft.getInstance().screen instanceof QuantumCrafterTermScreen<?> screen) {
            screen.clear();
        }
    }
}
