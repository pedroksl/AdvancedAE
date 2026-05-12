package net.pedroksl.advanced_ae.common.helpers;

import java.util.function.Predicate;

import net.minecraft.world.entity.EquipmentSlot;
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
        for (var slot : EquipmentSlot.values()) {
            if (this.locatable.test(player.getItemBySlot(slot))) {
                if (opener.open(player, player.getItemBySlot(slot))) {
                    return true;
                }
            }
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
