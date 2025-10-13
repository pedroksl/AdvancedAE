package net.pedroksl.advanced_ae.common.definitions;

import java.util.function.Supplier;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.entities.*;
import net.pedroksl.ae2addonlib.registry.BlockEntityRegistry;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.core.definitions.BlockDefinition;

@SuppressWarnings("unused")
public final class AAEBlockEntities extends BlockEntityRegistry {

    public static final AAEBlockEntities INSTANCE = new AAEBlockEntities();

    AAEBlockEntities() {
        super(AdvancedAE.MOD_ID);
    }

    public static final Supplier<BlockEntityType<AdvCraftingBlockEntity>> QUANTUM_COMPUTER_CORE = create(
            "quantum_core",
            AdvCraftingBlockEntity.class,
            AdvCraftingBlockEntity::new,
            AAEBlocks.QUANTUM_UNIT,
            AAEBlocks.QUANTUM_CORE,
            AAEBlocks.DATA_ENTANGLER,
            AAEBlocks.QUANTUM_STORAGE_128M,
            AAEBlocks.QUANTUM_STORAGE_256M,
            AAEBlocks.QUANTUM_ACCELERATOR,
            AAEBlocks.QUANTUM_MULTI_THREADER,
            AAEBlocks.QUANTUM_STRUCTURE);

    public static final Supplier<BlockEntityType<AdvPatternProviderEntity>> ADV_PATTERN_PROVIDER = create(
            "adv_pattern_provider",
            AdvPatternProviderEntity.class,
            AdvPatternProviderEntity::new,
            AAEBlocks.ADV_PATTERN_PROVIDER);
    public static final Supplier<BlockEntityType<SmallAdvPatternProviderEntity>> SMALL_ADV_PATTERN_PROVIDER = create(
            "small_adv_pattern_provider",
            SmallAdvPatternProviderEntity.class,
            SmallAdvPatternProviderEntity::new,
            AAEBlocks.SMALL_ADV_PATTERN_PROVIDER);

    public static final Supplier<BlockEntityType<ReactionChamberEntity>> REACTION_CHAMBER = create(
            "reaction_chamber", ReactionChamberEntity.class, ReactionChamberEntity::new, AAEBlocks.REACTION_CHAMBER);

    public static final Supplier<BlockEntityType<QuantumCrafterEntity>> QUANTUM_CRAFTER =
            create("quantum_craft", QuantumCrafterEntity.class, QuantumCrafterEntity::new, AAEBlocks.QUANTUM_CRAFTER);

    protected static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            BlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefs) {
        return create(AdvancedAE.MOD_ID, id, entityClass, factory, blockDefs);
    }
}
