package net.pedroksl.advanced_ae.common.items;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.block.AEBaseBlockItem;
import appeng.core.localization.Tooltips;

public class AAECraftingBlockItem extends AEBaseBlockItem {
    public AAECraftingBlockItem(Block block, Properties props) {
        super(block, getProps(block, props));
    }

    private static Properties getProps(Block block, Properties props) {
        if (block.equals(AAEBlocks.QUANTUM_CORE.block())
                || block.equals(AAEBlocks.QUANTUM_MULTI_THREADER.block())
                || block.equals(AAEBlocks.DATA_ENTANGLER.block())) {
            props.rarity(Rarity.EPIC);
        } else if (block.equals(AAEBlocks.QUANTUM_STORAGE_256M.block())) {
            props.rarity(Rarity.RARE);
        }
        return props;
    }

    public void addCheckedInformation(
            ItemStack stack, Item.TooltipContext context, List<Component> lines, TooltipFlag flag) {
        if (this.getBlock().equals(AAEBlocks.QUANTUM_ACCELERATOR.block())) {
            lines.add(Tooltips.of(
                    AAEText.AcceleratorThreads.text(AAEConfig.instance().getQuantumComputerAcceleratorThreads())
                            .withColor(AAEText.TOOLTIP_DEFAULT_COLOR)));
        } else if (this.getBlock().equals(AAEBlocks.QUANTUM_MULTI_THREADER.block())) {
            lines.add(Tooltips.of(AAEText.MultiThreaderMultiplication.text(
                            AAEConfig.instance().getQuantumComputerMultiThreaderMultiplication(),
                            AAEConfig.instance().getQuantumComputerMaxMultiThreaders())
                    .withColor(AAEText.TOOLTIP_DEFAULT_COLOR)));
        } else if (this.getBlock().equals(AAEBlocks.DATA_ENTANGLER.block())) {
            lines.add(Tooltips.of(AAEText.DataEntanglerMultiplication.text(
                            AAEConfig.instance().getQuantumComputerDataEntanglerMultiplication(),
                            AAEConfig.instance().getQuantumComputermaxDataEntanglers())
                    .withColor(AAEText.TOOLTIP_DEFAULT_COLOR)));
        } else if (this.getBlock().equals(AAEBlocks.QUANTUM_CORE.block())) {
            lines.add(Tooltips.of(AAEText.CoreTooltip.text(AAEConfig.instance().getQuantumComputerAcceleratorThreads())
                    .withColor(AAEText.TOOLTIP_DEFAULT_COLOR)));
        } else if (this.getBlock().equals(AAEBlocks.QUANTUM_STRUCTURE.block())) {
            lines.add(Tooltips.of(
                    AAEText.QuantumStructureTooltip.text(AAEConfig.instance().getQuantumComputerMaxSize())
                            .withColor(AAEText.TOOLTIP_DEFAULT_COLOR)));
        }
    }
}
