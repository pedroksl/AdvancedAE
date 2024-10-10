package net.pedroksl.advanced_ae.common.items.armors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.PoweredArmor;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.features.IGridLinkableHandler;
import appeng.api.ids.AEComponents;
import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.networking.GridHelper;
import appeng.menu.MenuOpener;
import appeng.menu.locator.ItemMenuHostLocator;

import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

public class QuantumArmorBase extends PoweredArmor implements GeoItem, IMenuItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public static final IGridLinkableHandler LINKABLE_HANDLER = new LinkableHandler();

    protected final List<UpgradeType> possibleUpgrades = new ArrayList<>();
    private List<UpgradeType> appliedUpgrades;

    public QuantumArmorBase(
            Holder<ArmorMaterial> material, Type type, Properties properties, DoubleSupplier powerCapacity) {
        super(material, type, properties, powerCapacity);
    }

    public List<UpgradeType> getPossibleUpgrades() {
        return possibleUpgrades;
    }

    protected List<UpgradeType> getAppliedUpgrades(ItemStack stack) {
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

    protected List<UpgradeType> getPassiveTickAbilities(ItemStack itemStack) {
        List<UpgradeType> abilityList = new ArrayList<>();
        getAppliedUpgrades(itemStack).forEach(up -> {
            if (up.applicationType == UpgradeType.ApplicationType.PASSIVE) abilityList.add(up);
        });
        return abilityList;
    }

    protected void tickUpgrades(Level level, Player player, ItemStack stack) {
        for (var upgrade : getAppliedUpgrades(stack)) {
            if (upgrade.applicationType == UpgradeType.ApplicationType.PASSIVE && isUpgradeEnabled(stack, upgrade)) {
                upgrade.ability.execute(level, player, stack);
            }
        }
    }

    public boolean isUpgradeEnabled(ItemStack stack, UpgradeType upgrade) {
        return stack.getOrDefault(AAEComponents.UPGRADE_TOGGLE.get(upgrade), false);
    }

    public boolean isUpgradePowered(ItemStack stack, UpgradeType upgrade) {
        return isUpgradePowered(stack, upgrade, null);
    }

    public boolean isUpgradePowered(ItemStack stack, UpgradeType upgrade, Level level) {
        // Use internal buffer
        var energy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energy != null && energy.getEnergyStored() > upgrade.getCost()) return true;

        // If that failed, try to pull from the grid
        if (level != null && stack.has(AEComponents.WIRELESS_LINK_TARGET)) {
            var host = GridHelper.getNodeHost(
                    level,
                    Objects.requireNonNull(stack.get(AEComponents.WIRELESS_LINK_TARGET))
                            .pos());
            if (host != null && host.getGridNode(null) != null) {
                var node = host.getGridNode(null).getGrid();
                var energyService = node.getEnergyService();
                var extracted =
                        energyService.extractAEPower(upgrade.getCost(), Actionable.SIMULATE, PowerMultiplier.CONFIG);
                return extracted >= upgrade.getCost() - 0.01;
            }
        }
        return false;
    }

    public boolean isUpgradeEnabledAndPowered(ItemStack stack, UpgradeType upgrade) {
        return isUpgradeEnabled(stack, upgrade) && isUpgradePowered(stack, upgrade);
    }

    public boolean isUpgradeAllowed(UpgradeType type) {
        return possibleUpgrades.contains(type);
    }

    public boolean hasUpgrade(ItemStack stack, UpgradeType type) {
        return stack.has(AAEComponents.UPGRADE_TOGGLE.get(type));
    }

    public boolean applyUpgrade(ItemStack stack, UpgradeType type) {
        if (!isUpgradeAllowed(type) || hasUpgrade(stack, type)) {
            return false;
        }

        getAppliedUpgrades(stack).add(type);
        stack.set(AAEComponents.UPGRADE_TOGGLE.get(type), true);
        if (type.getSettingType() == UpgradeType.SettingType.NUM_INPUT) {
            stack.set(AAEComponents.UPGRADE_VALUE.get(type), type.getSettings().maxValue);
        }
        if (type.getSettingType() == UpgradeType.SettingType.FILTER) {
            stack.set(AAEComponents.UPGRADE_FILTER.get(type), new ArrayList<>());
        }
        return true;
    }

    public boolean removeUpgrade(ItemStack stack, UpgradeType type) {
        if (getAppliedUpgrades(stack).contains(type)) {
            stack.remove(AAEComponents.UPGRADE_TOGGLE.get(type));
            stack.remove(AAEComponents.UPGRADE_VALUE.get(type));
            stack.remove(AAEComponents.UPGRADE_FILTER.get(type));
            getAppliedUpgrades(stack).remove(type);
            return true;
        }
        return false;
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

    // Create our armor model/renderer for Fabric and return it
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

    // Let's add our animation controller
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
