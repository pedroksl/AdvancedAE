package net.pedroksl.advanced_ae.common.parts;

import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.AAESingletons;

import appeng.api.AECapabilities;
import appeng.api.parts.IPartItem;
import appeng.api.parts.RegisterPartCapabilitiesEvent;

public class SmallAdvPatternProviderPart extends AdvPatternProviderPart {
    public SmallAdvPatternProviderPart(IPartItem<?> partItem) {
        super(partItem, 9);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void registerCapability(RegisterPartCapabilitiesEvent event) {
        event.register(
                AECapabilities.GENERIC_INTERNAL_INV,
                (part, context) -> part.logic.getReturnInv(),
                SmallAdvPatternProviderPart.class);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AAESingletons.SMALL_ADV_PATTERN_PROVIDER_PART);
    }
}
