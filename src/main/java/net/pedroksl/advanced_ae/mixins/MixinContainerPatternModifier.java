package net.pedroksl.advanced_ae.mixins;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AECraftingPattern;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.crafting.pattern.EncodedPatternItem;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.AppEngSlot;
import com.glodblock.github.extendedae.container.ContainerPatternModifier;
import com.glodblock.github.extendedae.util.Ae2Reflect;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsEncoder;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashMap;

@Mixin(ContainerPatternModifier.class)
public class MixinContainerPatternModifier extends AEBaseMenu {

	public MixinContainerPatternModifier(MenuType<?> menuType, int id, Inventory playerInventory, Object host) {
		super(menuType, id, playerInventory, host);
	}

	@Final
	@Shadow(remap = false)
	public AppEngSlot replaceTarget;

	@Final
	@Shadow(remap = false)
	public AppEngSlot replaceWith;

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

	/**
	 * @author pedroksl
	 * @reason Enabled replace method to keep adv patterns custom tags
	 */
	@Overwrite(remap = false)
	public void replace() {
		var replace = this.replaceTarget.getItem();
		var with = this.replaceWith.getItem();
		if (replace.isEmpty() || with.isEmpty()) {
			return;
		}
		for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
			var stack = slot.getItem();
			if (stack.getItem() instanceof EncodedPatternItem pattern) {
				var detail = pattern.decode(stack, this.getPlayer().level(), false);
				if (detail instanceof AEProcessingPattern process) {
					var input = process.getSparseInputs();
					var output = process.getOutputs();
					var replaceInput = new GenericStack[input.length];
					var replaceOutput = new GenericStack[output.length];
					this.replace(input, replaceInput, AEItemKey.of(replace), AEItemKey.of(with));
					this.replace(output, replaceOutput, AEItemKey.of(replace), AEItemKey.of(with));
					// Start edit
					if (detail instanceof AdvProcessingPattern advPattern) {
						var dirMap = advPattern.getDirectionMap();
						var newDirMap = new HashMap<AEKey, Direction>();
						for (var entry : dirMap.entrySet()) {
							if (AEItemKey.matches(entry.getKey(), replace)) {
								newDirMap.put(AEItemKey.of(with), entry.getValue());
							} else {
								newDirMap.put(entry.getKey(), entry.getValue());
							}
						}
						ItemStack newPattern = AdvPatternDetailsEncoder.encodeProcessingPattern(replaceInput,
								replaceOutput, newDirMap);
						slot.set(newPattern);
					} else {
						ItemStack newPattern = PatternDetailsHelper.encodeProcessingPattern(replaceInput, replaceOutput);
						slot.set(newPattern);
					}
					// End edit
				} else if (detail instanceof AECraftingPattern craft) {
					var input = craft.getSparseInputs();
					var output = craft.getPrimaryOutput();
					var replaceInput = new GenericStack[input.length];
					this.replace(input, replaceInput, AEItemKey.of(replace), AEItemKey.of(with));
					try {
						var newPattern = PatternDetailsHelper.encodeCraftingPattern(
								Ae2Reflect.getCraftRecipe(craft),
								itemize(replaceInput),
								itemize(output),
								craft.canSubstitute,
								craft.canSubstituteFluids
						);
						//noinspection DataFlowIssue
						var check = new AECraftingPattern(AEItemKey.of(newPattern), this.getPlayer().level());
						//noinspection ConstantValue
						if (check != null) {
							slot.set(newPattern);
						}
					} catch (Exception e) {
						// It is an invalid change
						return;
					}
				}
			}
		}
	}

	/**
	 * @author pedroksl
	 * @reason Enabled modify method to keep adv patterns custom tags
	 */
	@Overwrite(remap = false)
	public void modify(int scale, boolean div) {
		if (scale <= 0) {
			return;
		}
		for (var slot : this.getSlots(SlotSemantics.ENCODED_PATTERN)) {
			var stack = slot.getItem();
			if (stack.getItem() instanceof EncodedPatternItem pattern) {
				var detail = pattern.decode(stack, this.getPlayer().level(), false);
				if (detail instanceof AEProcessingPattern process) {
					var input = process.getSparseInputs();
					var output = process.getOutputs();
					if (checkModify(input, scale, div) && checkModify(output, scale, div)) {
						var mulInput = new GenericStack[input.length];
						var mulOutput = new GenericStack[output.length];
						modifyStacks(input, mulInput, scale, div);
						modifyStacks(output, mulOutput, scale, div);
						// Start edit
						if (detail instanceof AdvProcessingPattern advPattern) {
							var dirMap = advPattern.getDirectionMap();
							ItemStack newPattern = AdvPatternDetailsEncoder.encodeProcessingPattern(mulInput,
									mulOutput, dirMap);
							slot.set(newPattern);
						} else {
							ItemStack newPattern = PatternDetailsHelper.encodeProcessingPattern(mulInput, mulOutput);
							slot.set(newPattern);
						}
						// End edit
					}
				}
			}
		}
	}
}
