package net.pedroksl.advanced_ae.xmod.curios;

import java.util.Optional;

import net.minecraft.world.entity.player.Player;
import net.pedroksl.advanced_ae.xmod.Addons;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CuriosPlugin {

    public static Optional<ICuriosItemHandler> getCuriosInventory(Player player) {
        if (!Addons.CURIOS.isLoaded()) {
            return Optional.empty();
        }

        return CuriosApi.getCuriosInventory(player);
    }
}
