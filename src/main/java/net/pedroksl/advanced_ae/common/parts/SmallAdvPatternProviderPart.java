package net.pedroksl.advanced_ae.common.parts;

import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.api.parts.IPartItem;

public class SmallAdvPatternProviderPart extends AdvPatternProviderPart {
    public SmallAdvPatternProviderPart(IPartItem<?> partItem) {
        super(partItem, 9);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AAEItems.SMALL_ADV_PATTERN_PROVIDER);
    }
}
