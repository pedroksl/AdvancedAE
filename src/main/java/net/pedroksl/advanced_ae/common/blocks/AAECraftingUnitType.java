package net.pedroksl.advanced_ae.common.blocks;

import java.util.Locale;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;

import appeng.block.crafting.ICraftingUnitType;
import appeng.core.definitions.BlockDefinition;

public enum AAECraftingUnitType implements ICraftingUnitType, StringRepresentable {
    QUANTUM_UNIT(0),
    QUANTUM_CORE(256),
    QUANTUM_STORAGE_128(128),
    QUANTUM_STORAGE_256(256),
    DATA_ENTANGLER(0),
    QUANTUM_ACCELERATOR(0),
    QUANTUM_MULTI_THREADER(0),
    QUANTUM_STRUCTURE(0);

    public static final Codec<AAECraftingUnitType> CODEC = StringRepresentable.fromValues(AAECraftingUnitType::values);

    private final int storageMb;

    AAECraftingUnitType(int storageMb) {
        this.storageMb = storageMb;
    }

    @Override
    public long getStorageBytes() {
        return 1024L * 1024 * storageMb;
    }

    public int getStorageMultiplier() {
        return this == DATA_ENTANGLER ? AAEConfig.instance().getQuantumComputerDataEntanglerMultiplication() : 0;
    }

    @Override
    public int getAcceleratorThreads() {
        return switch (this) {
            case QUANTUM_ACCELERATOR, QUANTUM_CORE -> AAEConfig.instance().getQuantumComputerAcceleratorThreads();
            default -> 0;
        };
    }

    public int getAccelerationMultiplier() {
        return this == QUANTUM_MULTI_THREADER
                ? AAEConfig.instance().getQuantumComputerMultiThreaderMultiplication()
                : 0;
    }

    public BlockDefinition<?> getDefinition() {
        return switch (this) {
            case QUANTUM_UNIT -> AAEBlocks.QUANTUM_UNIT;
            case QUANTUM_CORE -> AAEBlocks.QUANTUM_CORE;
            case QUANTUM_STORAGE_128 -> AAEBlocks.QUANTUM_STORAGE_128M;
            case QUANTUM_STORAGE_256 -> AAEBlocks.QUANTUM_STORAGE_256M;
            case DATA_ENTANGLER -> AAEBlocks.DATA_ENTANGLER;
            case QUANTUM_ACCELERATOR -> AAEBlocks.QUANTUM_ACCELERATOR;
            case QUANTUM_MULTI_THREADER -> AAEBlocks.QUANTUM_MULTI_THREADER;
            case QUANTUM_STRUCTURE -> AAEBlocks.QUANTUM_STRUCTURE;
        };
    }

    @Override
    public Item getItemFromType() {
        return getDefinition().asItem();
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
