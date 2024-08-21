package net.pedroksl.advanced_ae.common.items;

import java.util.function.Supplier;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import appeng.block.crafting.CraftingBlockItem;

public class AAECraftingBlockItem extends CraftingBlockItem {
    public AAECraftingBlockItem(Block id, Properties props, Supplier<ItemLike> disassemblyExtra) {
        super(id, props, disassemblyExtra);
    }
}
