package net.pedroksl.advanced_ae.common;

import com.glodblock.github.glodium.util.GlodCodecs;
import com.glodblock.github.glodium.util.GlodUtil;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.blocks.ReactionChamberBlock;
import net.pedroksl.advanced_ae.common.blocks.SmallAdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;
import net.pedroksl.advanced_ae.common.entities.SmallAdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.items.AdvPatternEncoderItem;
import net.pedroksl.advanced_ae.common.items.AdvPatternProviderCapacityUpgradeItem;
import net.pedroksl.advanced_ae.common.items.AdvPatternProviderUpgradeItem;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.parts.SmallAdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import net.pedroksl.advanced_ae.common.patterns.EncodedAdvProcessingPattern;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.items.parts.PartItem;

public class AAESingletons {

    public static DataComponentType<EncodedAdvProcessingPattern> ENCODED_ADV_PROCESSING_PATTERN;
    public static DataComponentType<CompoundTag> STACK_TAG;

    public static AdvPatternProviderBlock ADV_PATTERN_PROVIDER;
    public static SmallAdvPatternProviderBlock SMALL_ADV_PATTERN_PROVIDER;
    public static PartItem<AdvPatternProviderPart> ADV_PATTERN_PROVIDER_PART;
    public static PartItem<SmallAdvPatternProviderPart> SMALL_ADV_PATTERN_PROVIDER_PART;
    public static ReactionChamberBlock REACTION_CHAMBER;

    public static Item ADV_PROCESSING_PATTERN;
    public static Item ADV_PATTERN_PROVIDER_UPGRADE;
    public static Item ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE;

    public static AdvPatternEncoderItem ADV_PATTERN_ENCODER;

    public static void init(AAERegistryHandler handler) {
        STACK_TAG = GlodUtil.getComponentType(CompoundTag.CODEC, GlodCodecs.NBT_STREAM_CODEC);

        ENCODED_ADV_PROCESSING_PATTERN =
                GlodUtil.getComponentType(EncodedAdvProcessingPattern.CODEC, EncodedAdvProcessingPattern.STREAM_CODEC);

        ADV_PATTERN_PROVIDER = new AdvPatternProviderBlock();
        SMALL_ADV_PATTERN_PROVIDER = new SmallAdvPatternProviderBlock();
        ADV_PATTERN_PROVIDER_PART =
                new PartItem<>(new Item.Properties(), AdvPatternProviderPart.class, AdvPatternProviderPart::new);
        SMALL_ADV_PATTERN_PROVIDER_PART = new PartItem<>(
                new Item.Properties(), SmallAdvPatternProviderPart.class, SmallAdvPatternProviderPart::new);

        REACTION_CHAMBER = new ReactionChamberBlock();

        ADV_PROCESSING_PATTERN = PatternDetailsHelper.encodedPatternItemBuilder(AdvProcessingPattern::new)
                .invalidPatternTooltip(AdvProcessingPattern::getInvalidPatternTooltip)
                .build();
        ADV_PATTERN_PROVIDER_UPGRADE = new AdvPatternProviderUpgradeItem();
        ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE = new AdvPatternProviderCapacityUpgradeItem();

        ADV_PATTERN_ENCODER = new AdvPatternEncoderItem();

        handler.comp("encoded_adv_processing_pattern", ENCODED_ADV_PROCESSING_PATTERN);
        handler.comp("generic_nbt", STACK_TAG);

        handler.block(
                "adv_pattern_provider",
                ADV_PATTERN_PROVIDER,
                AdvPatternProviderEntity.class,
                AdvPatternProviderEntity::new);
        handler.block(
                "small_adv_pattern_provider",
                SMALL_ADV_PATTERN_PROVIDER,
                SmallAdvPatternProviderEntity.class,
                SmallAdvPatternProviderEntity::new);
        handler.item("adv_pattern_provider_part", ADV_PATTERN_PROVIDER_PART);
        handler.item("small_adv_pattern_provider_part", SMALL_ADV_PATTERN_PROVIDER_PART);

        handler.block("reaction_chamber", REACTION_CHAMBER, ReactionChamberEntity.class, ReactionChamberEntity::new);

        handler.item("adv_processing_pattern", ADV_PROCESSING_PATTERN);
        handler.item("adv_pattern_encoder", ADV_PATTERN_ENCODER);
        handler.item("adv_pattern_provider_upgrade", ADV_PATTERN_PROVIDER_UPGRADE);
        handler.item("adv_pattern_provider_capacity_upgrade", ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE);
    }
}
