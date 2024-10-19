package net.pedroksl.advanced_ae.xmod.curios;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.items.IItemHandler;

import top.theillusivec4.curios.api.CuriosCapability;

public class CuriosPlugin {

    public static void addListeners(IEventBus eventBus) {}

    @Nullable
    public static IItemHandler getCuriosInventory(LivingEntity entity) {
        return entity.getCapability(CuriosCapability.ITEM_HANDLER);
    }
}
