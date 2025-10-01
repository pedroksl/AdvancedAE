package net.pedroksl.advanced_ae.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.block.crafting.ICraftingUnitType;

public class AAECraftingUnitBlock extends AAEAbstractCraftingUnitBlock<AdvCraftingBlockEntity> {

    public AAECraftingUnitBlock(ICraftingUnitType type) {
        super(getProps(type), type);
    }

    private static Properties getProps(ICraftingUnitType type) {
        var props = type == AAECraftingUnitType.STRUCTURE ? glassProps() : metalProps();
        if (type == AAECraftingUnitType.QUANTUM_CORE || type == AAECraftingUnitType.STRUCTURE) {
            props.lightLevel(state -> state.getValue(AAEAbstractCraftingUnitBlock.LIGHT_LEVEL));
            props.noOcclusion();
        }
        return props;
    }
}
