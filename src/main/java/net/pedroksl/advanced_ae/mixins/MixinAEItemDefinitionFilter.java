package net.pedroksl.advanced_ae.mixins;

import appeng.api.inventories.InternalInventory;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.ItemDefinition;
import appeng.util.inv.filter.AEItemDefinitionFilter;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AEItemDefinitionFilter.class)
public abstract class MixinAEItemDefinitionFilter {

	@Final
	@Shadow(remap = false)
	private ItemDefinition<?> definition;

	@Inject(method = "allowInsert", at = @At("HEAD"), cancellable = true, remap = false)
	protected void onAllowInsert(InternalInventory inv, int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (this.definition.isSameAs(AEItems.BLANK_PATTERN.stack())
				&& stack.is(AAEItemAndBlock.ADV_BLANK_PATTERN.asItem())) {
			cir.setReturnValue(true);
		}
	}
}
