package net.pedroksl.advanced_ae.xmod.curios;

import java.util.function.Consumer;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;

public class CuriosPlugin {

    public static void rechargeCurios(Player player, Consumer<ItemStack> recharge) {
        var cap = player.getCapability(CuriosCapability.ITEM_HANDLER);
        if (cap != null) {
            for (int i = 0; i < cap.size(); i++) {
                var is = cap.getResource(i).toStack();
                CuriosApi.getCurio(is).ifPresent(item -> {
                    recharge.accept(item.getStack());
                });
            }
        }
    }
}
