package net.pedroksl.advanced_ae.common.items;

import java.util.List;
import javax.annotation.Nonnull;

import com.glodblock.github.extendedae.common.parts.PartExPatternProvider;
import com.glodblock.github.extendedae.common.tileentities.TileExPatternProvider;
import com.glodblock.github.extendedae.util.FCUtil;
import com.glodblock.github.glodium.util.GlodUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.common.AAESingletons;
import net.pedroksl.advanced_ae.common.entities.AdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.entities.SmallAdvPatternProviderEntity;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;
import appeng.parts.crafting.PatternProviderPart;

public class AdvPatternProviderUpgradeItem extends Item {
    public AdvPatternProviderUpgradeItem() {
        super(new Item.Properties());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var entity = world.getBlockEntity(pos);
        if (entity != null) {
            var ctx = new BlockPlaceContext(context);
            var tClazz = entity.getClass();
            if (tClazz == PatternProviderBlockEntity.class || tClazz == TileExPatternProvider.class) {

                var originState = world.getBlockState(pos);
                var isSmall = tClazz == PatternProviderBlockEntity.class;

                var state = isSmall
                        ? AAESingletons.SMALL_ADV_PATTERN_PROVIDER.getStateForPlacement(ctx)
                        : AAESingletons.ADV_PATTERN_PROVIDER.getStateForPlacement(ctx);
                if (state == null) {
                    return InteractionResult.PASS;
                }
                for (var sp : originState.getValues().entrySet()) {
                    var pt = sp.getKey();
                    var va = sp.getValue();
                    try {
                        if (state.hasProperty(pt)) {
                            state = state.<Comparable, Comparable>setValue((Property) pt, va);
                        }
                    } catch (Exception ignore) {
                        // NO-OP
                    }
                }

                BlockEntityType<?> tileType = isSmall
                        ? GlodUtil.getTileType(SmallAdvPatternProviderEntity.class)
                        : GlodUtil.getTileType(AdvPatternProviderEntity.class);
                BlockEntity te = tileType.create(pos, state);
                FCUtil.replaceTile(world, pos, entity, te, state);
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;

            } else if (entity instanceof CableBusBlockEntity cable) {
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part instanceof AEBasePart basePart
                        && (part.getClass() == PatternProviderPart.class
                                || part.getClass() == PartExPatternProvider.class)) {
                    var side = basePart.getSide();
                    var contents = new CompoundTag();

                    var isSmall = part.getClass() == PatternProviderPart.class;

                    var partItem = isSmall
                            ? AAESingletons.SMALL_ADV_PATTERN_PROVIDER_PART
                            : AAESingletons.ADV_PATTERN_PROVIDER_PART;

                    part.writeToNBT(contents, world.registryAccess());
                    var p = cable.replacePart(partItem, side, context.getPlayer(), null);
                    if (p != null) {
                        p.readFromNBT(contents, world.registryAccess());
                    }
                } else {
                    return InteractionResult.PASS;
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.sidedSuccess(world.isClientSide);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(
            ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        tooltipComponents.add(Component.empty()
                .append("§7Upgrades a normal or extended pattern provider to the "
                        + "advanced version with the same amount of pattern slots"));
    }
}
