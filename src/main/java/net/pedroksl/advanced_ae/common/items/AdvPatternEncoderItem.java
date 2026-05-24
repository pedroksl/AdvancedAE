package net.pedroksl.advanced_ae.common.items;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.inventory.AdvPatternEncoderHost;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.items.AEBaseItem;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.locator.MenuLocators;

public class AdvPatternEncoderItem extends AEBaseItem implements IMenuItem {

    public AdvPatternEncoderItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    protected boolean checkPreconditions(ItemStack item) {
        return !item.isEmpty() && item.getItem() == this;
    }

    public boolean openFromInventory(Player player, ItemMenuHostLocator locator) {
        return openFromInventory(player, locator, false);
    }

    protected boolean openFromInventory(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        var is = locator.locateItem(player);

        if (!player.level().isClientSide() && checkPreconditions(is)) {
            return MenuOpener.open(AAEMenus.ADV_PATTERN_ENCODER.get(), player, locator, returningFromSubmenu);
        }
        return false;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        var is = player.getItemInHand(hand);

        if (!player.level().isClientSide() && checkPreconditions(is)) {
            if (MenuOpener.open(AAEMenus.ADV_PATTERN_ENCODER.get(), player, MenuLocators.forHand(player, hand))) {
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public @Nullable ItemMenuHost<AdvPatternEncoderItem> getMenuHost(
            Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new AdvPatternEncoderHost(this, player, locator);
    }
}
