package net.pedroksl.advanced_ae.common.blocks;

import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.block.crafting.ICraftingUnitType;

public class AAECraftingUnitBlock extends AAEAbstractCraftingUnitBlock<AdvCraftingBlockEntity> {

    public AAECraftingUnitBlock(ICraftingUnitType type) {
        super(getProps(type), type);
    }

    private static Properties getProps(ICraftingUnitType type) {
        var props = metalProps();
        if (type == AAECraftingUnitType.QUANTUM_CORE || type == AAECraftingUnitType.STRUCTURE) {
            props.lightLevel(state -> state.getValue(AAEAbstractCraftingUnitBlock.LIGHT_LEVEL));
        }
        if (type == AAECraftingUnitType.QUANTUM_CORE) {
            props.noOcclusion();
        }
        return props;
    }
}
