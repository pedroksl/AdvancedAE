package net.pedroksl.advanced_ae.mixins;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import com.glodblock.github.extendedae.common.inventory.PatternModifierInventory;
import com.glodblock.github.extendedae.container.ContainerPatternModifier;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetails;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsEncoder;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;

@Mixin(ContainerPatternModifier.class)
public class MixinContainerPatternModifier extends ContainerPatternModifier {

	public MixinContainerPatternModifier(int id, Inventory playerInventory, PatternModifierInventory host) {
		super(id, playerInventory, host);
	}

	@Shadow(remap = false)
	private void replace(GenericStack[] stacks, GenericStack[] des, AEKey replace, AEKey with) {
	}

	@Shadow(remap = false)
	private ItemStack[] itemize(GenericStack[] stacks) {
		return null;
	}

	@Shadow(remap = false)
	private ItemStack itemize(GenericStack stack) {
		return null;
	}

	@Shadow(remap = false)
	private boolean checkModify(GenericStack[] stacks, int scale, boolean div) {
		return false;
	}

	@Shadow(remap = false)
	private void modifyStacks(GenericStack[] stacks, GenericStack[] des, int scale, boolean div) {
	}

	@Overwrite(remap = false)
	public void replace() {
		ItemStack replace = this.replaceTarget.getItem();
		ItemStack with = this.replaceWith.getItem();
		if (!replace.isEmpty() && !with.isEmpty()) {
			Iterator var3 = this.getSlots(SlotSemantics.ENCODED_PATTERN).iterator();

			while (var3.hasNext()) {
				Slot slot = (Slot) var3.next();
				ItemStack stack = slot.getItem();
				Item var7 = stack.getItem();
				if (var7 instanceof EncodedPatternItem pattern) {
					IPatternDetails detail = pattern.decode(stack, this.getPlayer().level(), false);
					GenericStack[] input;
					GenericStack[] replaceInput;
					if (detail instanceof AEProcessingPattern process) {
						input = process.getSparseInputs();
						GenericStack[] output = process.getOutputs();
						replaceInput = new GenericStack[input.length];
						GenericStack[] replaceOutput = new GenericStack[output.length];
						this.replace(input, replaceInput, AEItemKey.of(replace), AEItemKey.of(with));
						this.replace(output, replaceOutput, AEItemKey.of(replace), AEItemKey.of(with));

						if (detail instanceof AdvProcessingPattern advPattern) {
							var dirMap = advPattern.getDirectionMap();
							ItemStack newPattern = AdvPatternDetailsEncoder.encodeProcessingPattern(replaceInput,
									replaceOutput, dirMap);
							slot.set(newPattern);
						} else {
							ItemStack newPattern = PatternDetailsHelper.encodeProcessingPattern(replaceInput, replaceOutput);
							slot.set(newPattern);
						}
					} else if (detail instanceof AECraftingPattern craft) {
						input = craft.getSparseInputs();
						GenericStack output = craft.getPrimaryOutput();
						replaceInput = new GenericStack[input.length];
						this.replace(input, replaceInput, AEItemKey.of(replace), AEItemKey.of(with));

						try {
							ItemStack newPattern = PatternDetailsHelper.encodeCraftingPattern(Ae2Reflect.getCraftRecipe(craft), this.itemize(replaceInput), this.itemize(output), craft.canSubstitute, craft.canSubstituteFluids);
							AECraftingPattern check = new AECraftingPattern(AEItemKey.of(newPattern), this.getPlayer().level());
							if (check != null) {
								slot.set(newPattern);
							}
						} catch (Exception var15) {
							return;
						}
					}
				}
			}

		}
	}

	@Overwrite(remap = false)
	public void modify(int scale, boolean div) {
		if (scale > 0) {
			Iterator var3 = this.getSlots(SlotSemantics.ENCODED_PATTERN).iterator();

			while (var3.hasNext()) {
				Slot slot = (Slot) var3.next();
				ItemStack stack = slot.getItem();
				Item var7 = stack.getItem();
				if (var7 instanceof EncodedPatternItem pattern) {
					IPatternDetails detail = pattern.decode(stack, this.getPlayer().level(), false);
					if (detail instanceof AEProcessingPattern process) {
						GenericStack[] input = process.getSparseInputs();
						GenericStack[] output = process.getOutputs();
						if (this.checkModify(input, scale, div) && this.checkModify(output, scale, div)) {
							GenericStack[] mulInput = new GenericStack[input.length];
							GenericStack[] mulOutput = new GenericStack[output.length];
							this.modifyStacks(input, mulInput, scale, div);
							this.modifyStacks(output, mulOutput, scale, div);
							if (detail instanceof AdvProcessingPattern advPattern) {
								var dirMap = advPattern.getDirectionMap();
								ItemStack newPattern = AdvPatternDetailsEncoder.encodeProcessingPattern(mulInput,
										mulOutput, dirMap);
								slot.set(newPattern);
							} else {
								ItemStack newPattern = PatternDetailsHelper.encodeProcessingPattern(mulInput, mulOutput);
								slot.set(newPattern);
							}
						}
					}
				}
			}

		}
	}
}
