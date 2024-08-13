package net.pedroksl.advanced_ae.mixins;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.ITerminalHost;
import appeng.menu.me.common.MEStorageMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.menu.slot.RestrictedInputSlot;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsDecoder;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsEncoder;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPatternItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;

@Mixin(PatternEncodingTermMenu.class)
public class MixinPatternEncodingTermMenu extends MEStorageMenu {

	@Final
	@Shadow(remap = false)
	private RestrictedInputSlot encodedPatternSlot;

	@Inject(
			method = "encodeProcessingPattern",
			at = @At(value = "INVOKE", ordinal = 6),
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILHARD,
			remap = false
	)
	private void onEncodeProcessingPattern(CallbackInfoReturnable<ItemStack> cir, GenericStack[] in, boolean v, GenericStack[] out) {
		if (this.encodedPatternSlot.hasItem()) {
			var encodedPattern = this.encodedPatternSlot.getItem();
			if (encodedPattern.is(AAEItemAndBlock.ADV_PROCESSING_PATTERN.asItem())) {

				var advPattern = AdvPatternDetailsDecoder.INSTANCE.decodePattern(encodedPattern,
						this.getPlayerInventory().player.level(), false);

				if (advPattern != null) {
					var dirMap = ((AdvProcessingPattern) advPattern).getDirectionMap();
					HashMap<AEKey, Direction> newDirMap = new HashMap<>();
					for (var input : in) {
						if (input == null) {
							continue;
						}

						if (dirMap.containsKey(input.what())) {
							newDirMap.put(input.what(), dirMap.get(input.what()));
						}
					}
					if (!newDirMap.isEmpty()) {
						cir.setReturnValue(AdvPatternDetailsEncoder.encodeProcessingPattern(in, out, newDirMap));
					}
				}
			}
		}
	}

	public MixinPatternEncodingTermMenu(MenuType<?> menuType, int id, Inventory ip, ITerminalHost host) {
		super(menuType, id, ip, host);
	}
}
