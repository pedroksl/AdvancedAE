package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.world.item.Item;
import net.pedroksl.advanced_ae.AAEConfig;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;

import appeng.block.crafting.ICraftingUnitType;
import appeng.core.definitions.BlockDefinition;

public enum AAECraftingUnitType implements ICraftingUnitType {
    QUANTUM_UNIT(0, "quantum_unit"),
    QUANTUM_CORE(256, "quantum_core"),
    STORAGE_128M(128, "quantum_storage_128"),
    STORAGE_256M(256, "quantum_storage_256"),
    STORAGE_MULTIPLIER(0, "data_entangler"),
    QUANTUM_ACCELERATOR(0, "quantum_accelerator"),
    MULTI_THREADER(0, "quantum_multi_threader"),
    STRUCTURE(0, "quantum_structure");

    private final int storageMb;
    private final String affix;

    AAECraftingUnitType(int storageMb, String affix) {
        this.storageMb = storageMb;
        this.affix = affix;
    }

    @Override
    public long getStorageBytes() {
        return 1024L * 1024 * storageMb;
    }

    public int getStorageMultiplier() {
        return this == STORAGE_MULTIPLIER ? AAEConfig.instance().getQuantumComputerDataEntanglerMultiplication() : 0;
    }

    @Override
    public int getAcceleratorThreads() {
        return switch (this) {
            case QUANTUM_ACCELERATOR, QUANTUM_CORE -> AAEConfig.instance().getQuantumComputerAcceleratorThreads();
            default -> 0;
        };
    }

    public int getAccelerationMultiplier() {
        return this == MULTI_THREADER ? AAEConfig.instance().getQuantumComputerMultiThreaderMultiplication() : 0;
    }

    public String getAffix() {
        return this.affix;
    }

    public BlockDefinition<?> getDefinition() {
        return switch (this) {
            case QUANTUM_UNIT -> AAEBlocks.QUANTUM_UNIT;
            case QUANTUM_CORE -> AAEBlocks.QUANTUM_CORE;
            case STORAGE_128M -> AAEBlocks.QUANTUM_STORAGE_128M;
            case STORAGE_256M -> AAEBlocks.QUANTUM_STORAGE_256M;
            case STORAGE_MULTIPLIER -> AAEBlocks.DATA_ENTANGLER;
            case QUANTUM_ACCELERATOR -> AAEBlocks.QUANTUM_ACCELERATOR;
            case MULTI_THREADER -> AAEBlocks.QUANTUM_MULTI_THREADER;
            case STRUCTURE -> AAEBlocks.QUANTUM_STRUCTURE;
        };
    }

    @Override
    public Item getItemFromType() {
        return getDefinition().asItem();
    }
}
