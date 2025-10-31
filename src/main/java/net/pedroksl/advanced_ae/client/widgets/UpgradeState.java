package net.pedroksl.advanced_ae.client.widgets;

import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

import appeng.api.stacks.GenericStack;

public record UpgradeState(
        UpgradeType type, boolean enabled, int currentValue, boolean extra, List<GenericStack> filter) {
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeState> STREAM_CODEC =
            StreamCodec.ofMember(UpgradeState::write, UpgradeState::decode);

    public UpgradeState(UpgradeType type, boolean enabled, int currentValue, boolean extra) {
        this(type, enabled, currentValue, extra, List.of());
    }

    public static UpgradeState decode(RegistryFriendlyByteBuf stream) {
        var type = stream.readEnum(UpgradeType.class);
        var enabled = stream.readBoolean();
        var currentValue = stream.readInt();
        var extra = stream.readBoolean();

        if (stream.readBoolean()) {
            var filter = GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(stream);
            return new UpgradeState(type, enabled, currentValue, extra, filter);
        } else {
            return new UpgradeState(type, enabled, currentValue, extra);
        }
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeEnum(type);
        data.writeBoolean(enabled);
        data.writeInt(currentValue);
        data.writeBoolean(extra);

        if (filter != null) {
            data.writeBoolean(true);
            GenericStack.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(data, filter);
        } else {
            data.writeBoolean(false);
        }
    }
}
