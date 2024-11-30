package net.pedroksl.advanced_ae.common.helpers;

import appeng.api.features.HotkeyAction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Predicate;

public record ArmorHotkeyAction(Predicate<ItemStack> locatable, Opener opener) implements HotkeyAction {
    public ArmorHotkeyAction(ItemLike item, Opener opener) {
        this((stack) -> stack.is(item.asItem()), opener);
    }

    public ArmorHotkeyAction(Predicate<ItemStack> locatable, Opener opener) {
        this.locatable = locatable;
        this.opener = opener;
    }

    @Override
    public boolean run(Player player) {
        var items = player.getArmorSlots();
        int i = 0;
        for (var item : items) {
            if (this.locatable.test(item)) {
                if (opener.open(player, Inventory.INVENTORY_SIZE + i, item)) {
                    return true;
                }
            }
            i++;
        }

        return false;
    }

    public Predicate<ItemStack> locatable() {
        return this.locatable;
    }

    public Opener opener() {
        return this.opener;
    }

    @FunctionalInterface
    public interface Opener {
        boolean open(Player var1, int inventorySlot, ItemStack stack);
    }
}
