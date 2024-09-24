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
import net.pedroksl.advanced_ae.common.definitions.AAEItems;

import appeng.api.orientation.BlockOrientation;
import appeng.block.crafting.PatternProviderBlock;
import appeng.core.AppEng;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.AE2BlockStateProvider;

public class AAEModelProvider extends AE2BlockStateProvider {
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

        quantumCrafterModel();

        // CRAFTING UNITS
        for (var type : AAECraftingUnitType.values()) {
            if (type == AAECraftingUnitType.QUANTUM_CORE || type == AAECraftingUnitType.STRUCTURE) {
                continue;
            }
            basicCraftingBlockModel(type);
        }

        var type = AAECraftingUnitType.STRUCTURE;
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

        interfaceOrProviderPart(AAEItems.ADV_PATTERN_PROVIDER);
        interfaceOrProviderPart(AAEItems.SMALL_ADV_PATTERN_PROVIDER);

        // PATTERN PROVIDER
        patternProvider(AAEBlocks.ADV_PATTERN_PROVIDER);
        patternProvider(AAEBlocks.SMALL_ADV_PATTERN_PROVIDER);
    }

    private void basicItem(ItemDefinition<?> item) {
        itemModels().basicItem(item.asItem());
    }

    private void basicBlock(BlockDefinition<?> block) {
        var model = cubeAll(block.block());
        simpleBlock(block.block(), model);
        simpleBlockItem(block.block(), model);
    }

    private void basicCraftingBlockModel(AAECraftingUnitType type) {
        var craftingBlock = type.getDefinition().block();
        var blockModel = models().cubeAll(
                        "block/crafting/" + type.getAffix(), AdvancedAE.makeId("block/crafting/" + type.getAffix()));
        simpleBlockItem(craftingBlock, blockModel);
        simpleBlock(craftingBlock, blockModel);
    }

    private void interfaceOrProviderPart(ItemDefinition<?> part) {
        var id = part.id().getPath();
        var partName = id.substring(0, id.lastIndexOf('_'));
        var front = AdvancedAE.makeId("part/" + partName);
        var back = AdvancedAE.makeId("part/" + partName + "_back");
        var sides = AdvancedAE.makeId("part/" + partName + "_sides");

        models().singleTexture(
                        "part/" + id,
                        AppEng.makeId("part/pattern_provider_base"),
                        "sides_status",
                        AppEng.makeId("part/monitor_sides_status"))
                .texture("sides", sides)
                .texture("front", front)
                .texture("back", back)
                .texture("particle", back);
        itemModels()
                .singleTexture("item/" + id, AppEng.makeId("item/cable_pattern_provider"), "sides", sides)
                .texture("front", front)
                .texture("back", back);
    }

    private void patternProvider(BlockDefinition<?> block) {
        var patternProviderNormal = cubeAll(block.block());
        simpleBlockItem(block.block(), patternProviderNormal);

        var blockName = block.id().getPath();
        var patternProviderOriented = models().cubeBottomTop(
                        "block/" + blockName + "_oriented",
                        AdvancedAE.makeId("block/" + blockName + "_alt"),
                        AdvancedAE.makeId("block/" + blockName + "_back"),
                        AdvancedAE.makeId("block/" + blockName + "_front"));
        multiVariantGenerator(block, Variant.variant())
                .with(PropertyDispatch.property(PatternProviderBlock.PUSH_DIRECTION)
                        .generate((dir) -> {
                            var forward = dir.getDirection();
                            if (forward == null) {
                                return Variant.variant()
                                        .with(VariantProperties.MODEL, patternProviderNormal.getLocation());
                            } else {
                                var orientation = BlockOrientation.get(forward);
                                return applyRotation(
                                        Variant.variant()
                                                .with(VariantProperties.MODEL, patternProviderOriented.getLocation()),
                                        // + 90 because the default model is oriented UP, while block orientation
                                        // assumes NORTH
                                        orientation.getAngleX() + 90,
                                        orientation.getAngleY(),
                                        0);
                            }
                        }));
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
