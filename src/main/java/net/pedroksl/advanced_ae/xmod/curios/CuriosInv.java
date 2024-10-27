package net.pedroksl.advanced_ae.xmod.curios;

import java.util.Optional;

import net.minecraft.world.entity.player.Player;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CuriosInv {

    public static Optional<ICuriosItemHandler> getCuriosInventory(Player player) {
        return CuriosApi.getCuriosInventory(player);
    }
}
