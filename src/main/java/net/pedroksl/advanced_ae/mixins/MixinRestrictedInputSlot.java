package net.pedroksl.advanced_ae.mixins;

import appeng.api.inventories.InternalInventory;
import appeng.core.definitions.AEItems;
import appeng.menu.slot.RestrictedInputSlot;
import appeng.menu.slot.RestrictedInputSlot.PlacableItemType;
import appeng.menu.slot.AppEngSlot;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RestrictedInputSlot.class)
public class MixinRestrictedInputSlot extends AppEngSlot {

	@Inject(method = "mayPlace", at = @At(value = "RETURN", ordinal = 8), cancellable = true)
	protected void onMayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		System.out.println("mixin method");
		cir.setReturnValue(AEItems.BLANK_PATTERN.isSameAs(stack)
				|| stack.is(AAEItemAndBlock.ADV_BLANK_PATTERN.asItem()));
	}

	public MixinRestrictedInputSlot(RestrictedInputSlot.PlacableItemType valid, InternalInventory inv, int invSlot) {
		super(inv, invSlot);
	}
}
