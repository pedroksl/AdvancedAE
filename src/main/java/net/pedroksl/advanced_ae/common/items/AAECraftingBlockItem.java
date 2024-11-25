package net.pedroksl.advanced_ae.common.items;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEConfig;
import net.pedroksl.advanced_ae.common.definitions.AAEText;

import appeng.block.AEBaseBlockItem;
import appeng.core.localization.Tooltips;

public class AAECraftingBlockItem extends AEBaseBlockItem {
    public AAECraftingBlockItem(Block block, Properties props) {
        super(block, props);
    }

    public void addCheckedInformation(ItemStack itemStack, Level level, List<Component> lines, TooltipFlag flag) {
        if (this.getBlock().equals(AAEBlocks.QUANTUM_ACCELERATOR.block())) {
            lines.add(Tooltips.of(
                    AAEText.AcceleratorThreads.text(AAEConfig.instance().getQuantumComputerAcceleratorThreads())
                            .withStyle(AAEText.TOOLTIP_DEFAULT_COLOR)));
        } else if (this.getBlock().equals(AAEBlocks.QUANTUM_MULTI_THREADER.block())) {
            lines.add(Tooltips.of(AAEText.MultiThreaderMultiplication.text(
                            AAEConfig.instance().getQuantumComputerMultiThreaderMultiplication(),
                            AAEConfig.instance().getQuantumComputerMaxMultiThreaders())
                    .withStyle(AAEText.TOOLTIP_DEFAULT_COLOR)));
        } else if (this.getBlock().equals(AAEBlocks.DATA_ENTANGLER.block())) {
            lines.add(Tooltips.of(AAEText.DataEntanglerMultiplication.text(
                            AAEConfig.instance().getQuantumComputerDataEntanglerMultiplication(),
                            AAEConfig.instance().getQuantumComputermaxDataEntanglers())
                    .withStyle(AAEText.TOOLTIP_DEFAULT_COLOR)));
        } else if (this.getBlock().equals(AAEBlocks.QUANTUM_CORE.block())) {
            lines.add(Tooltips.of(AAEText.CoreTooltip.text(AAEConfig.instance().getQuantumComputerAcceleratorThreads())
                    .withStyle(AAEText.TOOLTIP_DEFAULT_COLOR)));
        } else if (this.getBlock().equals(AAEBlocks.QUANTUM_STRUCTURE.block())) {
            lines.add(Tooltips.of(
                    AAEText.QuantumStructureTooltip.text(AAEConfig.instance().getQuantumComputerMaxSize())
                            .withStyle(AAEText.TOOLTIP_DEFAULT_COLOR)));
        }
    }
}
