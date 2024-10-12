package net.pedroksl.advanced_ae.client.widgets;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeSettings;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.stacks.GenericStack;

public record UpgradeState(
        UpgradeType type,
        UpgradeSettings settings,
        boolean enabled,
        int currentValue,
        @Nullable List<GenericStack> filter) {
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeState> STREAM_CODEC =
            StreamCodec.ofMember(UpgradeState::write, UpgradeState::decode);

    public UpgradeState(UpgradeType type, UpgradeSettings settings, boolean enabled, int currentValue) {
        this(type, settings, enabled, currentValue, List.of());
    }

    public static UpgradeState decode(RegistryFriendlyByteBuf stream) {
        var type = stream.readEnum(UpgradeType.class);
        var settings = UpgradeSettings.STREAM_CODEC.decode(stream);
        var enabled = stream.readBoolean();
        var currentValue = stream.readInt();

        if (stream.readBoolean()) {
            var filter = GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(stream);
            return new UpgradeState(type, settings, enabled, currentValue, filter);
        } else {
            return new UpgradeState(type, settings, enabled, currentValue);
        }
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeEnum(type);
        UpgradeSettings.STREAM_CODEC.encode(data, settings);
        data.writeBoolean(enabled);
        data.writeInt(currentValue);

        if (filter != null) {
            data.writeBoolean(true);
            GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(data, filter);
        }
    }
}
