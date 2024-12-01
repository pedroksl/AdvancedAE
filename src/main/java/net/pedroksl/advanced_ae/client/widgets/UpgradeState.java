package net.pedroksl.advanced_ae.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeSettings;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.stacks.GenericStack;

public record UpgradeState(
        UpgradeType type,
        UpgradeSettings settings,
        boolean enabled,
        int currentValue,
        @Nullable List<GenericStack> filter) {

    public UpgradeState(UpgradeType type, UpgradeSettings settings, boolean enabled, int currentValue) {
        this(type, settings, enabled, currentValue, List.of());
    }

    public static UpgradeState fromBytes(FriendlyByteBuf stream) {
        var type = stream.readEnum(UpgradeType.class);
        var settings = UpgradeSettings.fromBytes(stream);
        var enabled = stream.readBoolean();
        var currentValue = stream.readInt();

        var size = stream.readInt();
        List<GenericStack> filter = new ArrayList<>();
        for (var i = 0; i < size; i++) {
            filter.add(GenericStack.readBuffer(stream));
        }

        return new UpgradeState(type, settings, enabled, currentValue, filter);
    }

    public void toBytes(FriendlyByteBuf data) {
        data.writeEnum(type);
        settings.toBytes(data);
        data.writeBoolean(enabled);
        data.writeInt(currentValue);

        if (filter != null) {
            data.writeInt(filter.size());
            for (var i = 0; i < filter.size(); i++) {
                GenericStack.writeBuffer(filter.get(i), data);
            }
        }
    }
}
