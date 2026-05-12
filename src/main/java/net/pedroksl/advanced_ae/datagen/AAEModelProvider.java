package net.pedroksl.advanced_ae.datagen;

import static appeng.core.AppEng.makeId;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;

import java.util.Locale;

import net.minecraft.client.color.item.Constant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.client.item.QuantumArmorItemModel;
import net.pedroksl.advanced_ae.client.renderer.QuantumComputerModel;
import net.pedroksl.advanced_ae.common.blocks.AAEAbstractCraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.blocks.QuantumCrafterBlock;
import net.pedroksl.advanced_ae.common.blocks.ReactionChamberBlock;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEFluids;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.ae2addonlib.datagen.AE2AddonModelProvider;

import appeng.api.util.AEColor;
import appeng.api.util.AEColorVariant;
import appeng.client.api.model.parts.StaticPartModel;
import appeng.client.model.StatusIndicatorPartModel;
import appeng.client.render.AEColorItemTintSource;
import appeng.core.definitions.ItemDefinition;
import appeng.datagen.providers.models.PartModelOutput;
import appeng.items.parts.ColoredPartItem;
import appeng.items.parts.PartItem;

public class AAEModelProvider extends AE2AddonModelProvider {

    public AAEModelProvider(
            BlockModelGenerators blockModels, ItemModelGenerators itemModels, PartModelOutput partModels) {
        super(blockModels, itemModels, partModels);
    }

    @Override
    protected void register() {
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
        //        basicItem(AAEItems.QUANTUM_CRAFTER_WIRELESS_TERMINAL);

        quantumArmor(AAEItems.QUANTUM_HELMET);
        quantumArmor(AAEItems.QUANTUM_CHESTPLATE);
        quantumArmor(AAEItems.QUANTUM_LEGGINGS);
        quantumArmor(AAEItems.QUANTUM_BOOTS);

        stairsBlock(AAEBlocks.QUANTUM_ALLOY_STAIRS, AAEBlocks.QUANTUM_ALLOY_BLOCK);
        slabBlock(AAEBlocks.QUANTUM_ALLOY_SLAB, AAEBlocks.QUANTUM_ALLOY_BLOCK);
        wall(AAEBlocks.QUANTUM_ALLOY_WALL, new Material(AdvancedAE.makeId("block/quantum_alloy_block"), false));

        for (var card : AAEItems.getQuantumCards()) {
            basicItem(card, "item/upgrades/" + card.id().getPath());
        }

        // CRAFTING UNITS
        for (var type : AAECraftingUnitType.values()) {
            craftingUnitModel(type);
        }

        reactionChamberModel();
        quantumCrafterModel();

        partItem(AAEItems.ADV_PATTERN_PROVIDER);
        partItem(AAEItems.SMALL_ADV_PATTERN_PROVIDER);

        partModels.composite(
                AAEItems.THROUGHPUT_MONITOR,
                new StaticPartModel.Unbaked(makeId("part/monitor_base")),
                new StatusIndicatorPartModel.Unbaked(
                        makeId("part/monitor_bright_on"),
                        makeId("part/monitor_bright_off"),
                        makeId("part/monitor_bright_off")));
        customPartItemModel(AAEItems.THROUGHPUT_MONITOR.asItem());

        partModels.composite(
                AAEItems.QUANTUM_CRAFTER_TERMINAL,
                new StaticPartModel.Unbaked(makeId("part/display_base")),
                new StatusIndicatorPartModel.Unbaked(
                        AdvancedAE.makeId("part/quantum_crafter_terminal_on"),
                        AdvancedAE.makeId("part/quantum_crafter_terminal_on"),
                        AdvancedAE.makeId("part/quantum_crafter_terminal_off")));
        customPartItemModel(AAEItems.QUANTUM_CRAFTER_TERMINAL.asItem());
        //		partItem(AAEItems.QUANTUM_CRAFTER_TERMINAL);

        partItem(AAEItems.STOCK_EXPORT_BUS, true);
        partItem(AAEItems.IMPORT_EXPORT_BUS, true);
        partItem(AAEItems.ADVANCED_IO_BUS, true);

        simpleBlockAndItem(AAEBlocks.QUANTUM_ALLOY_BLOCK);

        // PATTERN PROVIDER
        patternProvider(AAEBlocks.ADV_PATTERN_PROVIDER);
        patternProvider(AAEBlocks.SMALL_ADV_PATTERN_PROVIDER);

        // Fluids
        for (var fluid : AAEFluids.INSTANCE.getFluids()) {
            waterBasedFluid(fluid);
        }
    }

    private void craftingUnitModel(AAECraftingUnitType type) {
        var block = type.getDefinition().block();
        var formedModel = customBlockStateModel(new QuantumComputerModel.Unbaked(type));

        if (type == AAECraftingUnitType.QUANTUM_CORE) {
            var coreModel = AdvancedAE.makeId("block/crafting/quantum_core");
            var coreOnModel = AdvancedAE.makeId("block/crafting/quantum_core_formed_on");

            blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                    .with(PropertyDispatch.initial(
                                    AAEAbstractCraftingUnitBlock.FORMED,
                                    AAEAbstractCraftingUnitBlock.POWERED,
                                    AAEAbstractCraftingUnitBlock.MULTIBLOCKED)
                            .generate((formed, powered, multiblocked) -> {
                                if (powered && multiblocked) {
                                    return formedModel;
                                } else if (powered && formed) {
                                    return plainVariant(coreOnModel);
                                } else {
                                    return plainVariant(coreModel);
                                }
                            })));
            blockModels.registerSimpleItemModel(block.asItem(), coreOnModel);
        } else if (type == AAECraftingUnitType.QUANTUM_STRUCTURE) {
            var itemModel = ModelTemplates.CUBE_ALL.create(
                    block,
                    new TextureMapping()
                            .put(
                                    TextureSlot.ALL,
                                    new Material(AdvancedAE.makeId("block/crafting/" + type.getSerializedName()))),
                    modelOutput);

            blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, formedModel));
            blockModels.registerSimpleItemModel(block.asItem(), itemModel);
        } else {
            simpleUnitBlockStateModel(type);
        }
    }

    private void simpleUnitBlockStateModel(AAECraftingUnitType type) {
        Identifier id = AdvancedAE.makeId("block/crafting/" + type.name().toLowerCase(Locale.ROOT));
        var unformedModel = ModelTemplates.CUBE_ALL.create(id, TextureMapping.cube(new Material(id)), modelOutput);
        var formedModel = customBlockStateModel(new QuantumComputerModel.Unbaked(type));

        var block = type.getDefinition().block();
        blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(AAEAbstractCraftingUnitBlock.FORMED)
                        .select(false, plainVariant(unformedModel))
                        .select(true, formedModel)));
        blockModels.registerSimpleItemModel(block.asItem(), unformedModel);
    }

    private void reactionChamberModel() {
        var blockModel = AdvancedAE.makeId("block/reaction_chamber");
        var blockModelOn = AdvancedAE.makeId("block/reaction_chamber_on");

        var block = AAEBlocks.REACTION_CHAMBER.block();

        blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(ReactionChamberBlock.WORKING)
                        .select(false, plainVariant(blockModel))
                        .select(true, plainVariant(blockModelOn))));

        blockModels.registerSimpleItemModel(block, blockModel);
    }

    private void quantumCrafterModel() {
        var grid = new Material(AdvancedAE.makeId("block/quantum_crafter_grid"), false);
        var gridOn = new Material(AdvancedAE.makeId("block/quantum_crafter_grid_on"), false);
        var bottom = new Material(AdvancedAE.makeId("block/quantum_crafter_bottom"), false);
        var sides = new Material(AdvancedAE.makeId("block/quantum_crafter_side"), false);

        var block = AAEBlocks.QUANTUM_CRAFTER.block();
        var blockModel = ModelTemplates.CUBE_BOTTOM_TOP.create(
                block,
                new TextureMapping()
                        .put(TextureSlot.TOP, grid)
                        .put(TextureSlot.NORTH, grid)
                        .put(TextureSlot.BOTTOM, bottom)
                        .put(TextureSlot.SIDE, sides),
                modelOutput);
        var blockModelOn = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(
                block,
                "_on",
                new TextureMapping()
                        .put(TextureSlot.TOP, gridOn)
                        .put(TextureSlot.NORTH, gridOn)
                        .put(TextureSlot.BOTTOM, bottom)
                        .put(TextureSlot.SIDE, sides),
                modelOutput);

        blockStateOutput.accept(MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(QuantumCrafterBlock.WORKING)
                        .select(false, plainSpinnableVariant(blockModel))
                        .select(true, plainSpinnableVariant(blockModelOn)))
                .withUnbaked(createFacingSpinDispatch()));
        // TODO: item model
    }

    private static StatusIndicatorPartModel.Unbaked partStatusIndicator(Identifier baseModel) {
        return new StatusIndicatorPartModel.Unbaked(
                baseModel.withSuffix("_has_channel"), baseModel.withSuffix("_on"), baseModel.withSuffix("_off"));
    }

    private void terminal(ItemLike part, Identifier modelBase) {
        Identifier localId = Identifier.fromNamespaceAndPath(AdvancedAE.MOD_ID, modelBase.getPath());
        partModels.composite(
                part,
                new StaticPartModel.Unbaked(makeId("part/display_base")),
                new StatusIndicatorPartModel.Unbaked(
                        localId.withSuffix("_on"), localId.withSuffix("_on"), localId.withSuffix("_off")),
                partStatusIndicator(makeId("part/display_status")));
    }

    private void customPartItemModel(PartItem<?> item) {
        var color = AEColor.TRANSPARENT;
        if (item instanceof ColoredPartItem<?> coloredPartItem) {
            color = coloredPartItem.getColor();
        }

        itemModels.itemModelOutput.accept(
                item.asItem(),
                ItemModelUtils.tintedModel(
                        ModelLocationUtils.getModelLocation(item),
                        new Constant(-1),
                        new AEColorItemTintSource(color, AEColorVariant.DARK),
                        new AEColorItemTintSource(color, AEColorVariant.MEDIUM),
                        new AEColorItemTintSource(color, AEColorVariant.BRIGHT),
                        new AEColorItemTintSource(color, AEColorVariant.MEDIUM_BRIGHT)));
    }

    private void quantumArmor(ItemDefinition<?> item) {
        if (!(item.asItem() instanceof QuantumArmorBase)) return;

        var id = item.id().getPath();
        Identifier baseTexture = AdvancedAE.makeId("item/" + id + "_base");
        Identifier tintTexture = AdvancedAE.makeId("item/" + id + "_tint");

        var model = ModelTemplates.TWO_LAYERED_ITEM.create(
                ModelLocationUtils.getModelLocation(item.asItem()),
                TextureMapping.layered(new Material(baseTexture), new Material(tintTexture)),
                itemModels.modelOutput);

        itemModels.itemModelOutput.accept(item.asItem(), new QuantumArmorItemModel.Unbaked(model));
    }
}
