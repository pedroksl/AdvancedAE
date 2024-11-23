package net.pedroksl.advanced_ae.common;

import appeng.items.parts.PartItem;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.blocks.AdvPatternProviderBlock;
import net.pedroksl.advanced_ae.common.blocks.QuantumCrafterBlock;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import net.pedroksl.advanced_ae.common.items.AdvPatternEncoderItem;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPatternItem;

public class AAEItemAndBlock {

	public static AAECraftingUnitBlock QUANTUM_UNIT;
	public static AAECraftingUnitBlock QUANTUM_CORE;
    public static AAECraftingUnitBlock QUANTUM_STORAGE_128M;
    public static AAECraftingUnitBlock QUANTUM_STORAGE_256M;
    public static AAECraftingUnitBlock DATA_ENTANGLER;
    public static AAECraftingUnitBlock QUANTUM_ACCELERATOR;
    public static AAECraftingUnitBlock QUANTUM_MULTI_THREADER;
	public static AAECraftingUnitBlock QUANTUM_STRUCTURE;

	public static AdvPatternProviderBlock ADV_PATTERN_PROVIDER;
	public static PartItem<AdvPatternProviderPart> ADV_PATTERN_PROVIDER_PART;

	public static AdvProcessingPatternItem ADV_PROCESSING_PATTERN;
	public static AdvPatternEncoderItem ADV_PATTERN_ENCODER;

	public static Item SHATTERED_SINGULARITY;

	public static void init(AAERegistryHandler handler) {
		QUANTUM_UNIT = new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_UNIT);
		QUANTUM_CORE = new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_CORE);
		QUANTUM_STORAGE_128M = new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_128M);
		QUANTUM_STORAGE_256M = new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_256M);
		DATA_ENTANGLER = new AAECraftingUnitBlock(AAECraftingUnitType.STORAGE_MULTIPLIER);
		QUANTUM_ACCELERATOR = new AAECraftingUnitBlock(AAECraftingUnitType.QUANTUM_ACCELERATOR);
		QUANTUM_MULTI_THREADER = new AAECraftingUnitBlock(AAECraftingUnitType.MULTI_THREADER);
		QUANTUM_STRUCTURE = new AAECraftingUnitBlock(AAECraftingUnitType.STRUCTURE);

		ADV_PATTERN_PROVIDER = new AdvPatternProviderBlock();
		ADV_PATTERN_PROVIDER_PART = new PartItem<>(new Item.Properties(), AdvPatternProviderPart.class,
				AdvPatternProviderPart::new);

		ADV_PROCESSING_PATTERN = new AdvProcessingPatternItem(new Item.Properties().stacksTo(1));
		ADV_PATTERN_ENCODER = new AdvPatternEncoderItem(new Item.Properties());

		SHATTERED_SINGULARITY = new Item(new Item.Properties());

		handler.block("quantum_unit", QUANTUM_UNIT, AdvCraftingBlockEntity.class, AdvCraftingBlockEntity::new);
		handler.block("quantum_core", QUANTUM_CORE, AdvCraftingBlockEntity.class, AdvCraftingBlockEntity::new);
		handler.block("quantum_storage_128", QUANTUM_STORAGE_128M, AdvCraftingBlockEntity.class, AdvCraftingBlockEntity::new);
		handler.block("quantum_storage_256", QUANTUM_STORAGE_256M, AdvCraftingBlockEntity.class, AdvCraftingBlockEntity::new);
		handler.block("data_entangler", DATA_ENTANGLER, AdvCraftingBlockEntity.class, AdvCraftingBlockEntity::new);
		handler.block("quantum_accelerator", QUANTUM_ACCELERATOR, AdvCraftingBlockEntity.class, AdvCraftingBlockEntity::new);
		handler.block("quantum_multi_threader", QUANTUM_MULTI_THREADER, AdvCraftingBlockEntity.class, AdvCraftingBlockEntity::new);
		handler.block("quantum_structure", QUANTUM_STRUCTURE, AdvCraftingBlockEntity.class, AdvCraftingBlockEntity::new);

		handler.block("adv_pattern_provider", ADV_PATTERN_PROVIDER, AdvPatternProviderEntity.class,
				AdvPatternProviderEntity::new);
		handler.item("adv_pattern_provider_part", ADV_PATTERN_PROVIDER_PART);

		handler.item("adv_processing_pattern", ADV_PROCESSING_PATTERN);
		handler.item("adv_pattern_encoder", ADV_PATTERN_ENCODER);
		handler.item("shattered_singularity", SHATTERED_SINGULARITY);
	}
}
