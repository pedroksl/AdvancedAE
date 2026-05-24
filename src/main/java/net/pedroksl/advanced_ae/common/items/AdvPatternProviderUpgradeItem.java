package net.pedroksl.advanced_ae.common.items;

import java.util.function.Consumer;
import javax.annotation.Nonnull;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.eae.ExtendedAEPlugin;
import net.pedroksl.ae2addonlib.util.BlockUpgradeItem;

import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;
import appeng.parts.crafting.PatternProviderPart;

public class AdvPatternProviderUpgradeItem extends BlockUpgradeItem {

    public AdvPatternProviderUpgradeItem(Properties properties) {
        super(properties);
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
            if (tClazz == PatternProviderBlockEntity.class
                    || (Addons.EXTENDEDAE.isLoaded() && ExtendedAEPlugin.isEntityProvider(tClazz))) {

                var originState = world.getBlockState(pos);
                var isSmall = tClazz == PatternProviderBlockEntity.class;

                var state = isSmall
                        ? AAEBlocks.SMALL_ADV_PATTERN_PROVIDER.block().getStateForPlacement(ctx)
                        : AAEBlocks.ADV_PATTERN_PROVIDER.block().getStateForPlacement(ctx);
                if (state == null) {
                    return InteractionResult.PASS;
                }
                for (var sp : originState.getValues().toList()) {
                    var pt = sp.property();
                    var va = sp.value();
                    try {
                        if (state.hasProperty(pt)) {
                            state = state.<Comparable, Comparable>setValue((Property) pt, va);
                        }
                    } catch (Exception ignore) {
                        // NO-OP
                    }
                }

                BlockEntityType<?> tileType = isSmall
                        ? AAEBlockEntities.SMALL_ADV_PATTERN_PROVIDER.get()
                        : AAEBlockEntities.ADV_PATTERN_PROVIDER.get();
                BlockEntity te = tileType.create(pos, state);
                replaceTile(world, pos, entity, te, state);
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;

            } else if (entity instanceof CableBusBlockEntity cable) {
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part instanceof AEBasePart basePart
                        && (part.getClass() == PatternProviderPart.class
                                || (Addons.EXTENDEDAE.isLoaded())
                                        && ExtendedAEPlugin.isPartProvider(part.getClass()))) {
                    var side = basePart.getSide();

                    var isSmall = part.getClass() == PatternProviderPart.class;

                    var partItem =
                            isSmall ? AAEItems.SMALL_ADV_PATTERN_PROVIDER.get() : AAEItems.ADV_PATTERN_PROVIDER.get();

                    var components = basePart.getBlockEntity().collectComponents();
                    var p = cable.replacePart(partItem, side, context.getPlayer(), null);
                    if (p != null) {
                        p.getBlockEntity().setComponents(components);
                    }
                } else {
                    return InteractionResult.PASS;
                }
                context.getItemInHand().shrink(1);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(
            ItemStack itemStack,
            TooltipContext context,
            TooltipDisplay display,
            Consumer<Component> builder,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        builder.accept(AAEText.PatternProviderUpgrade.text().withColor(AAEText.TOOLTIP_DEFAULT_COLOR));
    }
}
