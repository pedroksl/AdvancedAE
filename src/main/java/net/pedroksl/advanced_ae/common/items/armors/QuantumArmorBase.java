package net.pedroksl.advanced_ae.common.items.armors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.BlockHitResult;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.PoweredItem;

import appeng.api.features.IGridLinkableHandler;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;

import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class QuantumArmorBase extends PoweredItem implements GeoItem, IMenuItem, IUpgradeableItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final IGridLinkableHandler LINKABLE_HANDLER = new LinkableHandler();

    protected final List<UpgradeType> possibleUpgrades = new ArrayList<>();
    private List<UpgradeType> appliedUpgrades;

    public QuantumArmorBase(
            Holder<ArmorMaterial> material, Type type, Properties properties, DoubleSupplier powerCapacity) {
        super(material, type, properties.fireResistant(), powerCapacity);
    }

    @Override
    public void appendHoverText(
            ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, context, lines, advancedTooltips);

        if (stack.get(AEComponents.WIRELESS_LINK_TARGET) == null) {
            lines.add(Tooltips.of(GuiText.Unlinked, Tooltips.RED));
        } else {
            lines.add(Tooltips.of(GuiText.Linked, Tooltips.GREEN));
        }
    }

    @Override
    public List<UpgradeType> getPossibleUpgrades() {
        return possibleUpgrades;
    }

    @Override
    public List<UpgradeType> getAppliedUpgrades(ItemStack stack) {
        if (appliedUpgrades == null) {
            appliedUpgrades = new ArrayList<>();
            for (var upgrade : possibleUpgrades) {
                if (hasUpgrade(stack, upgrade)) {
                    appliedUpgrades.add(upgrade);
                }
            }
        }
        return appliedUpgrades;
    }

    protected boolean checkPreconditions(ItemStack item) {
        return !item.isEmpty() && item.getItem() == this;
    }

    public boolean openFromEquipmentSlot(Player player, ItemMenuHostLocator locator) {
        return openFromEquipmentSlot(player, locator, false);
    }

    public boolean openFromEquipmentSlot(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        var is = locator.locateItem(player);

        if (!player.level().isClientSide() && checkPreconditions(is)) {
            return MenuOpener.open(AAEMenus.QUANTUM_ARMOR_CONFIG, player, locator, returningFromSubmenu);
        }
        return false;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(
                    @Nullable T livingEntity,
                    ItemStack itemStack,
                    @Nullable EquipmentSlot equipmentSlot,
                    @Nullable HumanoidModel<T> original) {
                if (this.renderer == null) this.renderer = new QuantumArmorRenderer();

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        /*
        controllers.add(new AnimationController<GeoAnimatable>(this, 20, state -> {
        				// Apply our generic idle animation.
        				// Whether it plays or not is decided down below.
        				state.getController().setAnimation(IDLE);

        				// Let's gather some data from the state to use below
        				// This is the entity that is currently wearing/holding the item
        				Entity entity = state.getData(DataTickets.ENTITY);

        				// We'll just have ArmorStands always animate, so we can return here
        				if (entity instanceof ArmorStand) return PlayState.CONTINUE;

        				// For this example, we only want the animation to play if the entity is wearing all pieces of the armor
        				// Let's collect the armor pieces the entity is currently wearing
        				Set<Item> wornArmor = new ObjectOpenHashSet<>();

        				for (ItemStack stack : ((Player) entity).getArmorSlots()) {
        								// We can stop immediately if any of the slots are empty
        								if (stack.isEmpty()) return PlayState.STOP;

        								wornArmor.add(stack.getItem());
        				}

        				// Check each of the pieces match our set
        				boolean isFullSet = wornArmor.containsAll(ObjectArrayList.of(
        												AAEItems.QUANTUM_BOOTS.get(),
        												AAEItems.QUANTUM_LEGGINGS.get(),
        												AAEItems.QUANTUM_ARMOR.get(),
        												AAEItems.QUANTUM_HELMET.get()));

        				// Play the animation if the full set is being worn, otherwise stop
        				return isFullSet ? PlayState.CONTINUE : PlayState.STOP;
        }));
        	*/
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        return new QuantumArmorMenuHost<>(
                this, player, locator, (p, subMenu) -> openFromEquipmentSlot(p, locator, true));
    }

    private static class LinkableHandler implements IGridLinkableHandler {
        @Override
        public boolean canLink(ItemStack itemStack) {
            return itemStack.getItem() instanceof QuantumArmorBase;
        }

        @Override
        public void link(ItemStack itemStack, GlobalPos pos) {
            itemStack.set(AEComponents.WIRELESS_LINK_TARGET, pos);
        }

        @Override
        public void unlink(ItemStack itemStack) {
            itemStack.remove(AEComponents.WIRELESS_LINK_TARGET);
        }
    }
}
