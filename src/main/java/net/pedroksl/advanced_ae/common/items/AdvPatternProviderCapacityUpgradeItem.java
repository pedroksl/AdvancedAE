package net.pedroksl.advanced_ae.common.items;

import java.util.function.Consumer;
import javax.annotation.Nonnull;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.Vec3;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEBlockEntities;
import net.pedroksl.advanced_ae.common.definitions.AAEBlocks;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.entities.SmallAdvPatternProviderEntity;
import net.pedroksl.advanced_ae.common.parts.SmallAdvPatternProviderPart;
import net.pedroksl.ae2addonlib.util.BlockUpgradeItem;

import appeng.api.parts.PartHelper;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.AEBasePart;

public class AdvPatternProviderCapacityUpgradeItem extends BlockUpgradeItem {

    public AdvPatternProviderCapacityUpgradeItem(Properties properties) {
        super(properties);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Nonnull
    @Override
    public InteractionResult useOn(@Nonnull UseOnContext context) {
        var pos = context.getClickedPos();
        var world = context.getLevel();
        var entity = world.getBlockEntity(pos);

        if (!(world instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        if (entity != null) {
            var ctx = new BlockPlaceContext(context);
            var tClazz = entity.getClass();
            if (tClazz == SmallAdvPatternProviderEntity.class) {
                var originState = world.getBlockState(pos);
                var state = AAEBlocks.ADV_PATTERN_PROVIDER.block().getStateForPlacement(ctx);
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
                BlockEntity te = AAEBlockEntities.ADV_PATTERN_PROVIDER.get().create(pos, state);
                replaceTile(world, pos, entity, te, state);
                context.getItemInHand().shrink(1);
                return InteractionResult.CONSUME;

            } else if (entity instanceof CableBusBlockEntity cable) {
                Vec3 hitVec = context.getClickLocation();
                Vec3 hitInBlock = new Vec3(hitVec.x - pos.getX(), hitVec.y - pos.getY(), hitVec.z - pos.getZ());
                var part = cable.getCableBus().selectPartLocal(hitInBlock).part;
                if (part instanceof AEBasePart basePart && (part.getClass() == SmallAdvPatternProviderPart.class)) {
                    var side = basePart.getSide();

                    var partItem = AAEItems.ADV_PATTERN_PROVIDER.get();

                    // Carry the old part's data (patterns, settings, upgrades) over to the new part.
                    // Copying the cable bus block entity's components is not enough, and replacing a part
                    // removes the old one without dropping its contents.
                    try (ProblemReporter.ScopedCollector reporter =
                            new ProblemReporter.ScopedCollector(AdvancedAE.LOGGER)) {
                        var output = TagValueOutput.createWithContext(reporter, world.registryAccess());
                        part.writeToNBT(output);
                        var p = PartHelper.setPart(serverLevel, pos, side, context.getPlayer(), partItem);
                        if (p != null) {
                            var input = TagValueInput.create(reporter, world.registryAccess(), output.buildResult());
                            p.readFromNBT(input);
                            // setPart already added the part to the world, so only refresh the patterns.
                            p.getLogic().updatePatterns();
                        }
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

        builder.accept(Component.empty()
                .append(AAEText.PatternProviderCapacityUpgrade.text().withColor(AAEText.TOOLTIP_DEFAULT_COLOR)));
    }
}
