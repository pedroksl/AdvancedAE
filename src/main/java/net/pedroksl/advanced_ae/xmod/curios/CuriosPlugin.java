package net.pedroksl.advanced_ae.xmod.curios;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

public class CuriosPlugin {

    public static LazyOptional<ICuriosItemHandler> getCuriosInventory(Player player) {
        return CuriosApi.getCuriosInventory(player);
    }
}
