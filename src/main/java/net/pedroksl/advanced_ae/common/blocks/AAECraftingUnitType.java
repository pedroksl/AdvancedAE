package net.pedroksl.advanced_ae.common.blocks;

import appeng.block.crafting.ICraftingUnitType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;

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
        return this == STORAGE_MULTIPLIER ? 4 : 0;
    }

    @Override
    public int getAcceleratorThreads() {
        return switch (this) {
            case QUANTUM_ACCELERATOR, QUANTUM_CORE -> 8;
            default -> 0;
        };
    }

    public int getAccelerationMultiplier() {
        return this == MULTI_THREADER ? 4 : 0;
    }

    public String getAffix() {
        return this.affix;
    }

    public Block getBlock() {
        return switch (this) {
            case QUANTUM_UNIT -> AAEItemAndBlock.QUANTUM_UNIT;
            case QUANTUM_CORE -> AAEItemAndBlock.QUANTUM_CORE;
            case STORAGE_128M -> AAEItemAndBlock.QUANTUM_STORAGE_128M;
            case STORAGE_256M -> AAEItemAndBlock.QUANTUM_STORAGE_256M;
            case STORAGE_MULTIPLIER -> AAEItemAndBlock.DATA_ENTANGLER;
            case QUANTUM_ACCELERATOR -> AAEItemAndBlock.QUANTUM_ACCELERATOR;
            case MULTI_THREADER -> AAEItemAndBlock.QUANTUM_MULTI_THREADER;
            case STRUCTURE -> AAEItemAndBlock.QUANTUM_STRUCTURE;
        };
    }

    @Override
    public Item getItemFromType() {
        return switch (this) {
            case QUANTUM_UNIT -> AAEItemAndBlock.QUANTUM_UNIT.asItem();
            case QUANTUM_CORE -> AAEItemAndBlock.QUANTUM_CORE.asItem();
            case STORAGE_128M -> AAEItemAndBlock.QUANTUM_STORAGE_128M.asItem();
            case STORAGE_256M -> AAEItemAndBlock.QUANTUM_STORAGE_256M.asItem();
            case STORAGE_MULTIPLIER -> AAEItemAndBlock.DATA_ENTANGLER.asItem();
            case QUANTUM_ACCELERATOR -> AAEItemAndBlock.QUANTUM_ACCELERATOR.asItem();
            case MULTI_THREADER -> AAEItemAndBlock.QUANTUM_MULTI_THREADER.asItem();
            case STRUCTURE -> AAEItemAndBlock.QUANTUM_STRUCTURE.asItem();
        };
    }
}
