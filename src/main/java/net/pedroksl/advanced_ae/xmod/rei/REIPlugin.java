package net.pedroksl.advanced_ae.xmod.rei;

import com.glodblock.github.glodium.recipe.stack.IngredientStack;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.recipes.ReactionChamberRecipe;
import net.pedroksl.advanced_ae.xmod.rei.recipes.REIReactionChamberCategory;
import net.pedroksl.advanced_ae.xmod.rei.recipes.REIReactionChamberDisplay;

import appeng.integration.modules.itemlists.CompatLayerHelper;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.util.ClientEntryStacks;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.DefaultInformationDisplay;

import dev.architectury.fluid.FluidStack;

@REIPluginClient
public class REIPlugin implements REIClientPlugin {
    public static final ResourceLocation TEXTURE = AdvancedAE.makeId("textures/guis/emi.png");

    @Override
    public String getPluginProviderName() {
        return "Advanced AE";
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }
        registry.registerRecipeFiller(
                ReactionChamberRecipe.class, ReactionChamberRecipe.TYPE, REIReactionChamberDisplay::new);
        addDescription(registry, AAEItems.SHATTERED_SINGULARITY, AAEText.ShatteredSingularityDescription.text());
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        if (CompatLayerHelper.IS_LOADED) {
            return;
        }
        registry.add(new REIReactionChamberCategory());
        registry.addWorkstations(REIReactionChamberDisplay.ID, EntryStacks.of(AAEBlocks.REACTION_CHAMBER));
    }

    private static void addDescription(DisplayRegistry registry, ItemLike stack, Component... desc) {
        DefaultInformationDisplay info = DefaultInformationDisplay.createFromEntry(
                EntryStacks.of(stack), stack.asItem().getDescription());
        info.lines(desc);
        registry.add(info);
    }

    public static EntryIngredient stackOf(IngredientStack.Item stack) {
        if (!stack.isEmpty()) {
            var stacks = stack.getIngredient().getItems();
            var result = EntryIngredient.builder(stacks.length);
            for (var ing : stacks) {
                if (!ing.isEmpty()) {
                    result.add(EntryStacks.of(ing.copyWithCount(stack.getAmount())));
                }
            }
            return result.build();
        }
        return EntryIngredient.empty();
    }

    public static EntryIngredient stackOf(IngredientStack.Fluid stack, float tankSize) {
        if (!stack.isEmpty()) {
            var stacks = stack.getIngredient().getStacks();
            var result = EntryIngredient.builder(stacks.length);
            for (var ing : stacks) {
                if (!ing.isEmpty()) {
                    EntryStack<FluidStack> f = EntryStacks.of(ing.getFluid(), stack.getAmount());
                    ClientEntryStacks.setFluidRenderRatio(f, (float) stack.getAmount() / tankSize);
                    result.add(f);
                }
            }
            return result.build();
        }
        return EntryIngredient.empty();
    }
}
