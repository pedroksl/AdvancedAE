package net.pedroksl.advanced_ae.common.definitions;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.ItemDefinition;
import appeng.items.AEBaseItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;

import java.util.ArrayList;

public final class AAECreativeTab {
    public static final DeferredRegister<CreativeModeTab> DR =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AdvancedAE.MOD_ID);

    static {
        DR.register("tab", () -> CreativeModeTab.builder()
                .title(AAEText.ModName.text())
                .icon(AAEBlocks.ADV_PATTERN_PROVIDER::stack)
                .displayItems(AAECreativeTab::populateTab)
                .build());
    }

    private static void populateTab(CreativeModeTab.ItemDisplayParameters params, CreativeModeTab.Output output) {
        var itemDefs = new ArrayList<ItemDefinition<?>>();
        itemDefs.addAll(AAEItems.getItems());
        itemDefs.addAll(
                AAEBlocks.getBlocks().stream().map(AAEBlockDefinition::item).toList());
        itemDefs.addAll(AAEFluids.getFluids().stream()
                .map(FluidDefinition::bucketItemId)
                .toList());

        for (var itemDef : itemDefs) {
            var item = itemDef.asItem();

            // For block items, the block controls the creative tab
            if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(output);
//            } else if (item instanceof PoweredItem poweredItem) {
//                poweredItem.addToMainCreativeTab(params, output);
            } else {
                output.accept(itemDef);
            }
        }
    }
}
