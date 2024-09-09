package net.pedroksl.advanced_ae.common.definitions;

import java.util.ArrayList;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;

import appeng.block.AEBaseBlock;
import appeng.block.AEBaseBlockItem;
import appeng.core.definitions.BlockDefinition;
import appeng.core.definitions.ItemDefinition;
import appeng.items.AEBaseItem;

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
                AAEBlocks.getBlocks().stream().map(BlockDefinition::item).toList());

        for (var itemDef : itemDefs) {
            var item = itemDef.asItem();

            // For block items, the block controls the creative tab
            if (item instanceof AEBaseBlockItem baseItem && baseItem.getBlock() instanceof AEBaseBlock baseBlock) {
                baseBlock.addToMainCreativeTab(params, output);
            } else if (item instanceof AEBaseItem baseItem) {
                baseItem.addToMainCreativeTab(params, output);
            } else {
                output.accept(itemDef);
            }
        }
    }
}
