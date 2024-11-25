package net.pedroksl.advanced_ae.common.definitions;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;

import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;

@SuppressWarnings("unused")
public final class AAEBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> DR =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AdvancedAE.MOD_ID);

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
    //    public static final Supplier<BlockEntityType<SmallAdvPatternProviderEntity>> SMALL_ADV_PATTERN_PROVIDER =
    // create(
    //            "small_adv_pattern_provider",
    //            SmallAdvPatternProviderEntity.class,
    //            SmallAdvPatternProviderEntity::new,
    //            AAEBlocks.SMALL_ADV_PATTERN_PROVIDER);

    //    public static final Supplier<BlockEntityType<ReactionChamberEntity>> REACTION_CHAMBER = create(
    //            "reaction_chamber", ReactionChamberEntity.class, ReactionChamberEntity::new,
    // AAEBlocks.REACTION_CHAMBER);

    public static final Supplier<BlockEntityType<QuantumCrafterEntity>> QUANTUM_CRAFTER =
            create("quantum_craft", QuantumCrafterEntity.class, QuantumCrafterEntity::new, AAEBlocks.QUANTUM_CRAFTER);

    @SuppressWarnings({"DataFlowIssue", "unchecked"})
    @SafeVarargs
    private static <T extends AEBaseBlockEntity> Supplier<BlockEntityType<T>> create(
            String id,
            Class<T> entityClass,
            BlockEntityFactory<T> factory,
            AAEBlockDefinition<? extends AEBaseEntityBlock<?>>... blockDefs) {
        if (blockDefs.length == 0) {
            throw new IllegalArgumentException();
        }

        return DR.register(id, () -> {
            var blocks = Arrays.stream(blockDefs).map(AAEBlockDefinition::block).toArray(AEBaseEntityBlock[]::new);

            var typeHolder = new AtomicReference<BlockEntityType<T>>();
            var type = BlockEntityType.Builder.of((pos, state) -> factory.create(typeHolder.get(), pos, state), blocks)
                    .build(null);
            typeHolder.set(type);

            AEBaseBlockEntity.registerBlockEntityItem(type, blockDefs[0].asItem());

            for (var block : blocks) {
                block.setBlockEntity(entityClass, type, null, null);
            }

            return type;
        });
    }

    private interface BlockEntityFactory<T extends AEBaseBlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }
}
