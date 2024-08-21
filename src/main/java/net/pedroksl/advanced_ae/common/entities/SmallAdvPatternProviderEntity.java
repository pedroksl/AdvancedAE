package net.pedroksl.advanced_ae.common.entities;

import com.glodblock.github.glodium.util.GlodUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroksl.advanced_ae.common.AAESingletons;

import appeng.api.stacks.AEItemKey;

public class SmallAdvPatternProviderEntity extends AdvPatternProviderEntity {

    public SmallAdvPatternProviderEntity(BlockPos pos, BlockState blockState) {
        super(
                GlodUtil.getTileType(
                        SmallAdvPatternProviderEntity.class,
                        SmallAdvPatternProviderEntity::new,
                        AAESingletons.SMALL_ADV_PATTERN_PROVIDER),
                pos,
                blockState,
                9);
    }

    @Override
    public AEItemKey getTerminalIcon() {
        return AEItemKey.of(AAESingletons.SMALL_ADV_PATTERN_PROVIDER);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(AAESingletons.SMALL_ADV_PATTERN_PROVIDER);
    }
}
