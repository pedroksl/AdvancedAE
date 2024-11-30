package net.pedroksl.advanced_ae.common.helpers;

import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagnetHelpers {

    public static AABB getBoundingBox(Vec3 pos, int range) {
        return new AABB(pos.x - range, pos.y - range, pos.z - range, pos.x + range, pos.y + range, pos.z + range);
    }

    public static boolean validEntities(ItemEntity item, Player player, List<GenericStack> filter, boolean blacklist) {
        if (item.isAlive()
                && (player instanceof ServerPlayer || item.tickCount > 1)
                && !item.getItem().isEmpty()) {
            // Don't pick if thrown by the player
            if (!(item.thrower == null || !item.thrower.equals(player.getUUID()) || !item.hasPickUpDelay()))
                return false;

            // Compatibility with demagnetization tool
            if (item.getPersistentData().contains("PreventRemoteMovement")) return false;

            if (filter.isEmpty()) return true;

            var filteredList =
                    filter.stream().filter(gen -> AEItemKey.of(item.getItem()).matches(gen));
            var containedInFilter = filteredList.findAny().isPresent();
            return (containedInFilter && !blacklist) || (!containedInFilter && blacklist);
        }

        return false;
    }
}
