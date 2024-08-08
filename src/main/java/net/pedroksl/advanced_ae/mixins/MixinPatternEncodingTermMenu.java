package net.pedroksl.advanced_ae.mixins;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.ITerminalHost;
import appeng.core.definitions.AEItems;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.parts.encoding.PatternEncodingLogic;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;
import net.pedroksl.advanced_ae.common.helpers.PatternDetailsEncoder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(PatternEncodingTermMenu.class)
public abstract class MixinPatternEncodingTermMenu extends MEStorageMenu {

	@Final
	@Shadow(remap = false)
	private PatternEncodingLogic encodingLogic;

	@Final
	@Shadow(remap = false)
	private RestrictedInputSlot blankPatternSlot;

	@Final
	@Shadow(remap = false)
	private CraftingRecipe currentRecipe;

	@Shadow(remap = false)
	public boolean substitute = false;

	@Shadow(remap = false)
	public boolean substituteFluids = true;

	@Inject(
			method = "isPattern",
			at = @At("HEAD"),
			cancellable = true,
			remap = false
	)
	protected void onIsPattern(ItemStack output, CallbackInfoReturnable<Boolean> cir) {
		if (output.isEmpty()) {
			cir.setReturnValue(false);
		}

		cir.setReturnValue(AEItems.BLANK_PATTERN.isSameAs(output)
				|| output.is(AAEItemAndBlock.ADV_BLANK_PATTERN.asItem()));
	}

	@Inject(
			method = "encodeCraftingPattern",
			at = @At(value = "RETURN", ordinal = 3),
			cancellable = true,
			remap = false,
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onEncodeCraftingPattern(CallbackInfoReturnable<ItemStack> cir, ItemStack[] ingredients, boolean valid,
	                                     ItemStack result) {
		var blankPattern = blankPatternSlot.getItem();
		System.out.print("encode crafting");
		if (AEItems.BLANK_PATTERN.isSameAs(blankPattern)) {
			System.out.print("is blank pattern");
			cir.setReturnValue(PatternDetailsHelper.encodeCraftingPattern(currentRecipe, ingredients, result, substitute,
					substituteFluids));
		} else {
			System.out.print("is advanced blank pattern");
			cir.setReturnValue(PatternDetailsEncoder.encodeCraftingPattern(currentRecipe, ingredients, result,
					substitute, substituteFluids));
		}
	}

	@Inject(
			method = "encodeProcessingPattern",
			at = @At("TAIL"),
			cancellable = true,
			remap = false,
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onEncodeProcessingPattern(CallbackInfoReturnable<ItemStack> cir, GenericStack[] inputs, boolean valid,
	                                       GenericStack[] outputs) {
		var blankPattern = this.blankPatternSlot.getItem();
		if (AEItems.BLANK_PATTERN.isSameAs(blankPattern)) {
			cir.setReturnValue(PatternDetailsHelper.encodeProcessingPattern(inputs, outputs));
		} else {
			cir.setReturnValue(PatternDetailsEncoder.encodeProcessingPattern(inputs, outputs));
		}
	}

	@Inject(
			method = "encodeSmithingTablePattern",
			at = @At("TAIL"),
			cancellable = true,
			remap = false,
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onEncodeSmithingPattern(CallbackInfoReturnable<ItemStack> cir, AEItemKey template, AEItemKey base,
	                                     AEItemKey addition, SimpleContainer container, Level level,
	                                     SmithingRecipe recipe,
	                                     AEItemKey output) {
		var blankPattern = this.blankPatternSlot.getItem();
		if (AEItems.BLANK_PATTERN.isSameAs(blankPattern)) {
			cir.setReturnValue(PatternDetailsHelper.encodeSmithingTablePattern(recipe, template, base, addition, output,
					encodingLogic.isSubstitution()));
		} else {
			cir.setReturnValue(PatternDetailsEncoder.encodeSmithingTablePattern(recipe, template, base, addition, output,
					encodingLogic.isSubstitution()));
		}
	}

	@Inject(
			method = "encodeStonecuttingPattern",
			at = @At("TAIL"),
			cancellable = true,
			remap = false,
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void onEncodeStonecuttingPattern(CallbackInfoReturnable<ItemStack> cir, AEItemKey input,
	                                         SimpleContainer container, Level level, StonecutterRecipe recipe, AEItemKey output) {
		var blankPattern = this.blankPatternSlot.getItem();
		if (AEItems.BLANK_PATTERN.isSameAs(blankPattern)) {
			cir.setReturnValue(PatternDetailsHelper.encodeStonecuttingPattern(recipe, input, output,
					encodingLogic.isSubstitution()));
		} else {
			cir.setReturnValue(PatternDetailsEncoder.encodeStonecuttingPattern(recipe, input, output,
					encodingLogic.isSubstitution()));
		}
	}


	protected MixinPatternEncodingTermMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host, boolean bindInventory) {
		super(menuType, id, ip, host, bindInventory);
	}
}
