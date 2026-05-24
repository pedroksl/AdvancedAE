package net.pedroksl.advanced_ae.xmod.emi;

import net.minecraft.world.item.crafting.*;

// @EmiEntrypoint
// public class EMIPlugin implements EmiPlugin {
//    public static final ResourceLocation TEXTURE = AdvancedAE.makeId("textures/guis/emi.png");
//
//    @Override
//    public void register(EmiRegistry registry) {
//        registry.addCategory(EMIReactionChamberRecipe.CATEGORY);
//        registry.addWorkstation(EMIReactionChamberRecipe.CATEGORY, EmiStack.of(AAEBlocks.REACTION_CHAMBER.stack()));
//        adaptRecipeType(registry, ReactionChamberRecipe.TYPE, EMIReactionChamberRecipe::new);
//        addInfo(registry, AAEBlocks.ADV_PATTERN_PROVIDER, AAEText.AdvPatternProviderEmiDesc.text());
//    }
//
//    private static <C extends RecipeInput, T extends Recipe<C>> void adaptRecipeType(
//            EmiRegistry registry, RecipeType<T> recipeType, Function<RecipeHolder<T>, ? extends EmiRecipe> adapter) {
//        registry.getRecipeManager().getAllRecipesFor(recipeType).stream()
//                .map(adapter)
//                .forEach(registry::addRecipe);
//    }
//
//    private static void addInfo(EmiRegistry registry, ItemLike item, Component... desc) {
//        registry.addRecipe(new EmiInfoRecipe(
//                List.of(EmiStack.of(item)), Arrays.stream(desc).toList(), null));
//    }
//
//    public static EmiIngredient stackOf(IngredientStack.Item stack) {
//        return !stack.isEmpty() ? EmiIngredient.of(stack.getIngredient(), stack.getAmount()) : EmiStack.EMPTY;
//    }
//
//    public static EmiIngredient stackOf(IngredientStack.Fluid stack) {
//        FluidIngredient ingredient = stack.getIngredient();
//        List<EmiIngredient> list = new ArrayList<>();
//        FluidStack[] stacks = ingredient.getStacks();
//        for (FluidStack fluid : stacks) {
//            list.add(EmiStack.of(fluid.getFluid(), stack.getAmount()));
//        }
//
//        return EmiIngredient.of(list, stack.getAmount());
//    }
// }
