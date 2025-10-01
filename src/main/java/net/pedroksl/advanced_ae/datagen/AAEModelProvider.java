package net.pedroksl.advanced_ae.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.ModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.internal.versions.neoforge.NeoForgeVersion;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.AAEAbstractCraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.blocks.QuantumCrafterBlock;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;
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
        basicItem(AAEItems.QUANTUM_INFUSED_DUST);
        basicItem(AAEItems.QUANTUM_ALLOY);
        basicItem(AAEItems.QUANTUM_ALLOY_PLATE);
        basicItem(AAEItems.QUANTUM_PROCESSOR_PRESS);
        basicItem(AAEItems.QUANTUM_PROCESSOR_PRINT);
        basicItem(AAEItems.QUANTUM_PROCESSOR);
        basicItem(AAEItems.QUANTUM_STORAGE_COMPONENT);
        basicItem(AAEItems.MONITOR_CONFIGURATOR);

        basicItem(AAEItems.QUANTUM_HELMET);
        basicItem(AAEItems.QUANTUM_CHESTPLATE);
        basicItem(AAEItems.QUANTUM_LEGGINGS);
        basicItem(AAEItems.QUANTUM_BOOTS);

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

        // PATTERN PROVIDER
        patternProvider(AAEBlocks.ADV_PATTERN_PROVIDER);
        patternProvider(AAEBlocks.SMALL_ADV_PATTERN_PROVIDER);

        // Fluids
        fluidBlocks();
        buckets();
    }

    private void basicItem(ItemDefinition<?> item) {
        basicItem(item, "");
    }

    private void basicItem(ItemDefinition<?> item, String texturePath) {
        if (texturePath.isEmpty()) itemModels().basicItem(item.asItem());
        else {
            String id = item.id().getPath();
            itemModels()
                    .singleTexture(
                            id, mcLoc("item/generated"), "layer0", AdvancedAE.makeId("item/" + texturePath + "/" + id));
        }
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
        interfaceOrProviderPart(part, false);
    }

    private void interfaceOrProviderPart(ItemDefinition<?> part, boolean isExport) {
        var id = part.id().getPath();
        var partName = id.substring(0, id.lastIndexOf('_'));
        var front = AdvancedAE.makeId("part/" + partName);
        var back = AdvancedAE.makeId("part/" + partName + "_back");
        var sides = AdvancedAE.makeId("part/" + partName + "_sides");

        var base = isExport ? AppEng.makeId("part/export_bus_base") : AppEng.makeId("part/pattern_provider_base");
        var itemBase = isExport ? AppEng.makeId("item/export_bus") : AppEng.makeId("item/cable_pattern_provider");

        models().singleTexture("part/" + id, base, "sidesStatus", AppEng.makeId("part/monitor_sides_status"))
                .texture("sides", sides)
                .texture("front", front)
                .texture("back", back)
                .texture("particle", back);
        itemModels()
                .singleTexture("item/" + id, itemBase, "sides", sides)
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
    protected void stairsBlock(
            BlockDefinition<StairBlock> stairs, String bottomTexture, String sideTexture, String topTexture) {
        String baseName = stairs.id().getPath();
        ResourceLocation side = AdvancedAE.makeId(sideTexture);
        ResourceLocation bottom = AdvancedAE.makeId(bottomTexture);
        ResourceLocation top = AdvancedAE.makeId(topTexture);
        ModelFile stairsModel = this.models().stairs(baseName, side, bottom, top);
        ModelFile stairsInner = this.models().stairsInner(baseName + "_inner", side, bottom, top);
        ModelFile stairsOuter = this.models().stairsOuter(baseName + "_outer", side, bottom, top);
        this.stairsBlock(stairs.block(), stairsModel, stairsInner, stairsOuter);
        this.simpleBlockItem(stairs.block(), stairsModel);
    }

    @Override
    protected void slabBlock(
            BlockDefinition<SlabBlock> slab,
            BlockDefinition<?> base,
            String bottomTexture,
            String sideTexture,
            String topTexture) {
        ResourceLocation side = AdvancedAE.makeId(sideTexture);
        ResourceLocation bottom = AdvancedAE.makeId(bottomTexture);
        ResourceLocation top = AdvancedAE.makeId(topTexture);
        BlockModelBuilder bottomModel = this.models().slab(slab.id().getPath(), side, bottom, top);
        this.simpleBlockItem(slab.block(), bottomModel);
        this.slabBlock(
                slab.block(),
                bottomModel,
                this.models().slabTop(slab.id().getPath() + "_top", side, bottom, top),
                this.models().getExistingFile(base.id()));
    }

    @Override
    protected void wall(BlockDefinition<WallBlock> block, String texture) {
        wallBlock(block.block(), AdvancedAE.makeId(texture));
        itemModels().wallInventory(block.id().getPath(), AdvancedAE.makeId(texture));
    }

    private void fluidBlocks() {
        for (var fluids : AAEFluids.getFluids()) {
            simpleBlock(
                    fluids.block(),
                    models().getBuilder(fluids.blockId().getId().getPath())
                            .texture("particle", AdvancedAE.makeId(ModelProvider.BLOCK_FOLDER + "/" + "water_still")));
        }
    }

    public void buckets() {
        for (var fluids : AAEFluids.getFluids()) {
            itemModels()
                    .withExistingParent(
                            fluids.bucketItemId().id().getPath(),
                            ResourceLocation.fromNamespaceAndPath(NeoForgeVersion.MOD_ID, "item/bucket"))
                    .customLoader(DynamicFluidContainerModelBuilder::begin)
                    .fluid(fluids.bucketItem().content);
        }
    }

    @Override
    public String getName() {
        return "Block States / Models";
    }
}
