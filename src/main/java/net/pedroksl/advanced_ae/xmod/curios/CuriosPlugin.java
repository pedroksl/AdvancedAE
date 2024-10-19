package net.pedroksl.advanced_ae.xmod.curios;

import java.util.Optional;

import net.minecraft.world.entity.LivingEntity;

import appeng.core.definitions.ItemDefinition;

import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

public class CuriosPlugin {
    public static Optional<SlotResult> findFirstCurio(LivingEntity entity, ItemDefinition<?> item) {
        return CuriosApi.getCuriosInventory(entity).flatMap(h -> h.findFirstCurio(item.get()));
    }
}
