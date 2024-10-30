package net.pedroksl.advanced_ae.common.helpers;

import java.util.function.Predicate;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import appeng.api.features.HotkeyAction;

public record ToggleUpgradeCardAction(Predicate<ItemStack> locatable, Opener opener) implements HotkeyAction {
    public ToggleUpgradeCardAction(ItemLike item, Opener opener) {
        this((stack) -> stack.is(item.asItem()), opener);
    }

    public ToggleUpgradeCardAction(Predicate<ItemStack> locatable, Opener opener) {
        this.locatable = locatable;
        this.opener = opener;
    }

    @Override
    public boolean run(Player player) {
        var items = player.getArmorSlots();
        int i = 0;
        for (var item : items) {
            if (this.locatable.test(item)) {
                if (opener.open(player, item)) {
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
        boolean open(Player player, ItemStack stack);
    }
}
