package net.pedroksl.advanced_ae.common.cluster;

import java.util.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;

import appeng.api.networking.IGrid;
import appeng.api.networking.events.GridCraftingCpuChange;
import appeng.me.cluster.IAEMultiBlock;
import appeng.me.cluster.MBCalculator;

public class AdvCraftingCPUCalculator extends MBCalculator<AdvCraftingBlockEntity, AdvCraftingCPUCluster> {

    public AdvCraftingCPUCalculator(AdvCraftingBlockEntity t) {
        super(t);
    }

    @Override
    public boolean checkMultiblockScale(BlockPos min, BlockPos max) {
        if (max.getX() - min.getX() > 5) {
            return false;
        }

        if (max.getY() - min.getY() > 5) {
            return false;
        }

        return max.getZ() - min.getZ() <= 5;
    }

    @Override
    public AdvCraftingCPUCluster createCluster(ServerLevel level, BlockPos min, BlockPos max) {
        return new AdvCraftingCPUCluster(min, max);
    }

    @Override
    public boolean verifyInternalStructure(ServerLevel level, BlockPos min, BlockPos max) {
        boolean core = false;
        boolean storage = false;
        boolean entangler = false;
        boolean multi = false;

        for (BlockPos blockPos : BlockPos.betweenClosed(min, max)) {
            final IAEMultiBlock<?> te = (IAEMultiBlock<?>) level.getBlockEntity(blockPos);

            if (te == null || !te.isValid()) {
                return false;
            }

            if (te instanceof AdvCraftingBlockEntity advEntity) {

                boolean isBoundary = blockPos.getX() == min.getX()
                        || blockPos.getY() == min.getY()
                        || blockPos.getZ() == min.getZ()
                        || blockPos.getX() == max.getX()
                        || blockPos.getY() == max.getY()
                        || blockPos.getZ() == max.getZ();

                switch ((AAECraftingUnitType) advEntity.getUnitBlock().type) {
                    case QUANTUM_CORE: {
                        if (!isBoundary && !core) {
                            core = true;
                        } else {
                            return false;
                        }
                        break;
                    }
                    case STRUCTURE: {
                        if (!isBoundary) {
                            return false;
                        }
                        break;
                    }
                    case STORAGE_MULTIPLIER: {
                        if (!isBoundary && !entangler) {
                            entangler = true;
                        } else {
                            return false;
                        }
                        break;
                    }
                    case MULTI_THREADER: {
                        if (!isBoundary && !multi) {
                            multi = true;
                        } else {
                            return false;
                        }
                        break;
                    }
                    default: {
                        if (isBoundary) {
                            return false;
                        }
                    }
                }

                if (!storage) {
                    storage = advEntity.getStorageBytes() > 0;
                }
            } else {
                return false;
            }
        }
        return storage && core;
    }

    @Override
    public void updateBlockEntities(AdvCraftingCPUCluster c, ServerLevel level, BlockPos min, BlockPos max) {
        for (BlockPos blockPos : BlockPos.betweenClosed(min, max)) {
            final AdvCraftingBlockEntity te = (AdvCraftingBlockEntity) level.getBlockEntity(blockPos);
            te.updateStatus(c);
            c.addBlockEntity(te);
        }

        c.done();

        final Iterator<AdvCraftingBlockEntity> i = c.getBlockEntities();
        while (i.hasNext()) {
            var gh = i.next();
            var n = gh.getGridNode();
            if (n != null) {
                final IGrid g = n.getGrid();
                g.postEvent(new GridCraftingCpuChange(n));
                return;
            }
        }
    }

    @Override
    public boolean isValidBlockEntity(BlockEntity te) {
        return te instanceof AdvCraftingBlockEntity;
    }
}
