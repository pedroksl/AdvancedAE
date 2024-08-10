package net.pedroksl.advanced_ae.common.items;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.items.AEBaseItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.inventory.AdvPatternEncoderInventory;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdvPatternEncoderItem extends AEBaseItem implements IMenuItem {


	public AdvPatternEncoderItem() {
		super(new Item.Properties().stacksTo(1));
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player p, @NotNull InteractionHand hand) {
		if (!level.isClientSide()) {
			MenuOpener.open(AdvPatternEncoderContainer.TYPE, p, MenuLocators.forHand(p, hand));
		}
		return new InteractionResultHolder<>(InteractionResult.sidedSuccess(level.isClientSide()), p.getItemInHand(hand));
	}

	@Override
	public @Nullable ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
		return new AdvPatternEncoderInventory(player, inventorySlot, stack);
	}

}
