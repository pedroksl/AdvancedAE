package net.pedroksl.advanced_ae.common.parts;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.PartModel;

public class SmallAdvPatternProviderPart extends AdvPatternProviderPart {

    public static final ResourceLocation MODEL_BASE = AdvancedAE.makeId("part/small_adv_pattern_provider_part");

    @PartModels
    public static final PartModel MODELS_OFF = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_off"));

    @PartModels
    public static final PartModel MODELS_ON = new PartModel(MODEL_BASE, AppEng.makeId("part/interface_on"));

    @PartModels
    public static final PartModel MODELS_HAS_CHANNEL =
            new PartModel(MODEL_BASE, AppEng.makeId("part/interface_has_channel"));

    public SmallAdvPatternProviderPart(IPartItem<?> partItem) {
        super(partItem, 9);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AAEItems.SMALL_ADV_PATTERN_PROVIDER);
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else {
            return this.isPowered() ? MODELS_ON : MODELS_OFF;
        }
    }
}
