package net.pedroksl.advanced_ae.common.blocks;

import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.block.crafting.ICraftingUnitType;

public class AAECraftingUnitBlock extends AAEAbstractCraftingUnitBlock<AdvCraftingBlockEntity> {

    public AAECraftingUnitBlock(Properties p, ICraftingUnitType type) {
        super(getProps(p, type), type);
    }

    private static Properties getProps(Properties p, ICraftingUnitType type) {
        var props = type == AAECraftingUnitType.QUANTUM_STRUCTURE ? glassProps(p) : metalProps(p);
        if (type == AAECraftingUnitType.QUANTUM_CORE || type == AAECraftingUnitType.QUANTUM_STRUCTURE) {
            props.lightLevel(state -> state.getValue(AAEAbstractCraftingUnitBlock.LIGHT_LEVEL));
            props.noOcclusion();
        }
        return props;
    }
}
