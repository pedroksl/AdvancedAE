package net.pedroksl.advanced_ae.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.AAEAbstractCraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.blocks.QuantumCrafterBlock;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.ae2addonlib.datagen.AE2AddonModelProvider;

public class AAEModelProvider extends AE2AddonModelProvider {
    public AAEModelProvider(PackOutput packOutput, ExistingFileHelper exFileHelper) {
        super(packOutput, AdvancedAE.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        basicItem(AAEItems.ADV_PROCESSING_PATTERN);
        basicItem(AAEItems.ADV_PATTERN_PROVIDER_UPGRADE);
        basicItem(AAEItems.ADV_PATTERN_PROVIDER_CAPACITY_UPGRADE);
        basicItem(AAEItems.ADV_PATTERN_ENCODER);
        basicItem(AAEItems.SHATTERED_SINGULARITY);
        basicItem(AAEItems.QUANTUM_INFUSED_DUST);
        basicItem(AAEItems.QUANTUM_ALLOY);
        basicItem(AAEItems.QUANTUM_ALLOY_PLATE);
        basicItem(AAEItems.QUANTUM_PROCESSOR_PRESS);
        basicItem(AAEItems.QUANTUM_PROCESSOR_PRINT);
        basicItem(AAEItems.QUANTUM_PROCESSOR);
        basicItem(AAEItems.QUANTUM_STORAGE_COMPONENT);
        basicItem(AAEItems.MONITOR_CONFIGURATOR);
        basicItem(AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL);

        coloredItem(AAEItems.QUANTUM_HELMET);
        coloredItem(AAEItems.QUANTUM_CHESTPLATE);
        coloredItem(AAEItems.QUANTUM_LEGGINGS);
        coloredItem(AAEItems.QUANTUM_BOOTS);

        stairsBlock(AAEBlocks.QUANTUM_ALLOY_STAIRS, AAEBlocks.QUANTUM_ALLOY_BLOCK);
        slabBlock(AAEBlocks.QUANTUM_ALLOY_SLAB, AAEBlocks.QUANTUM_ALLOY_BLOCK);
        wall(AAEBlocks.QUANTUM_ALLOY_WALL, "block/quantum_alloy_block");

        for (var card : AAEItems.getQuantumCards()) {
            basicItem(card, "upgrades");
        }

        quantumCrafterModel();

        // CRAFTING UNITS
        for (var type : AAECraftingUnitType.values()) {
            if (type == AAECraftingUnitType.QUANTUM_CORE || type == AAECraftingUnitType.STRUCTURE) {
                continue;
            }
            var craftingBlock = type.getDefinition().block();
            var name = type.getAffix();
            var blockModel = models().cubeAll("block/crafting/" + name, AdvancedAE.makeId("block/crafting/" + name));
            getVariantBuilder(craftingBlock)
                    .partialState()
                    .with(AAEAbstractCraftingUnitBlock.FORMED, false)
                    .setModels(new ConfiguredModel(blockModel))
                    .partialState()
                    .with(AAEAbstractCraftingUnitBlock.FORMED, true)
                    .setModels(new ConfiguredModel(models().getBuilder("block/crafting/" + name + "_formed")));
            simpleBlockItem(craftingBlock, blockModel);
        }

        interfaceOrProviderPart(AAEItems.ADV_PATTERN_PROVIDER);
        interfaceOrProviderPart(AAEItems.SMALL_ADV_PATTERN_PROVIDER);
        interfaceOrProviderPart(AAEItems.STOCK_EXPORT_BUS, true);
        interfaceOrProviderPart(AAEItems.IMPORT_EXPORT_BUS, true);
        interfaceOrProviderPart(AAEItems.ADVANCED_IO_BUS, true);

        // PATTERN PROVIDER
        patternProvider(AAEBlocks.ADV_PATTERN_PROVIDER);
        patternProvider(AAEBlocks.SMALL_ADV_PATTERN_PROVIDER);

        // Fluids
        for (var fluid : AAEFluids.INSTANCE.getFluids()) {
            waterBaseFluid(fluid);
        }
    }

    private void basicCraftingBlockModel(AAECraftingUnitType type) {
        var craftingBlock = type.getDefinition().block();
        var blockModel = models().cubeAll(
                        "block/crafting/" + type.getAffix(), AdvancedAE.makeId("block/crafting/" + type.getAffix()));
        simpleBlockItem(craftingBlock, blockModel);
        simpleBlock(craftingBlock, blockModel);
    }

    private void quantumCrafterModel() {
        var grid = AdvancedAE.makeId("block/quantum_crafter_grid");
        var gridOn = AdvancedAE.makeId("block/quantum_crafter_grid_on");
        var bottom = AdvancedAE.makeId("block/quantum_crafter_bottom");
        var sides = AdvancedAE.makeId("block/quantum_crafter_side");

        var block = AAEBlocks.QUANTUM_CRAFTER.block();
        var blockModel =
                models().cubeBottomTop("quantum_crafter", sides, bottom, grid).texture("north", grid);
        var blockModelOn = models().cubeBottomTop("quantum_crafter_on", sides, bottom, gridOn)
                .texture("north", gridOn);
        multiVariantGenerator(AAEBlocks.QUANTUM_CRAFTER, Variant.variant())
                .with(PropertyDispatch.property(QuantumCrafterBlock.WORKING).generate((working) -> {
                    var model = working ? blockModelOn : blockModel;
                    return Variant.variant().with(VariantProperties.MODEL, model.getLocation());
                }))
                .with(createFacingSpinDispatch());
        simpleBlockItem(block, blockModelOn);
    }

    @Override
    public String getName() {
        return "Block States / Models";
    }
}
