package net.pedroksl.advanced_ae.network.packet;

import com.glodblock.github.glodium.network.packet.IMessage;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.client.Hotkeys;
import net.pedroksl.advanced_ae.common.definitions.AAEHotkeys;

import appeng.core.AELog;
import appeng.core.localization.PlayerMessages;

public class AAEHotkeyPacket implements IMessage<AAEHotkeyPacket> {
    private String hotkey;

    public AAEHotkeyPacket() {}

    public AAEHotkeyPacket(String hotkey) {
        this.hotkey = hotkey;
    }

    public AAEHotkeyPacket(Hotkeys.AAEHotkey hotkey) {
        this.hotkey = hotkey.name();
    }

    @Override
    public void fromBytes(FriendlyByteBuf stream) {
        this.hotkey = stream.readUtf();
    }

    @Override
    public void toBytes(FriendlyByteBuf data) {
        data.writeUtf(this.hotkey);
    }

    @Override
    public void onMessage(Player player) {
        var actions = AAEHotkeys.REGISTRY.get(hotkey);
        if (actions == null) {
            player.sendSystemMessage(PlayerMessages.UnknownHotkey.text()
                    .copy()
                    .append(Component.translatable("key.advanced_ae." + hotkey)));
            AELog.warn("Player %s tried using unknown hotkey \"%s\"", player, hotkey);
            return;
        }

        for (var action : actions) {
            if (action.run(player)) {
                break;
            }
        }
    }

    @Override
    public Class<AAEHotkeyPacket> getPacketClass() {
        return AAEHotkeyPacket.class;
    }

    @Override
    public boolean isClient() {
        return false;
    }
}
