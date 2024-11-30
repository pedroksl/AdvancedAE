package net.pedroksl.advanced_ae.common.items.armors;

import appeng.api.implementations.menuobjects.IMenuItem;
import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.core.localization.GuiText;
import appeng.core.localization.Tooltips;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pedroksl.advanced_ae.client.Hotkeys;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEHotkeys;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.xmod.Addons;
import net.pedroksl.advanced_ae.xmod.apoth.ApoEnchPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

public class QuantumArmorBase extends PoweredItem implements GeoItem, IMenuItem, IUpgradeableItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    protected final List<UpgradeType> possibleUpgrades = new ArrayList<>();
    private List<UpgradeType> appliedUpgrades;

    protected static final String MENU_TYPE = "aae$menutype";

    protected enum MenuId {
        STANDARD(0),
        WORKBENCH(1),
        CRAFTING(2);

        public final int id;

        MenuId(int id) {
            this.id = id;
        }
    }

    public QuantumArmorBase(
            ArmorMaterial material, Type type, Properties properties, DoubleSupplier powerCapacity) {
        super(material, type, properties.fireResistant().rarity(Rarity.EPIC).stacksTo(1), powerCapacity);
    }

    protected void registerUpgrades(UpgradeType... upgrades) {
        this.possibleUpgrades.addAll(Arrays.asList(upgrades));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(
            ItemStack stack, @NotNull Level context, List<Component> lines, TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, context, lines, advancedTooltips);

        var hotkey = Hotkeys.getHotkeyMapping(AAEHotkeys.Keys.ARMOR_CONFIG.getId());
        if (hotkey != null) {
            lines.add(AAEText.QuantumArmorHotkeyTooltip.text(
                            hotkey.mapping().getTranslatedKeyMessage().copy().withStyle(ChatFormatting.GRAY))
                    .withStyle(ChatFormatting.DARK_GRAY));
        }

        appendExtraHoverText(stack, context, lines, advancedTooltips);

        if (getLinkedPosition(stack) == null) {
            lines.add(Tooltips.of(GuiText.Unlinked, Tooltips.RED));
        } else {
            lines.add(Tooltips.of(GuiText.Linked, Tooltips.GREEN));
        }

        lines.add(Component.empty());
        lines.add(AAEText.QuantumArmorTooltip.text().withStyle(Tooltips.NORMAL_TOOLTIP_TEXT));
        for (var upgrade : possibleUpgrades) {
            var upgradeComponent =
                    Component.translatable(upgrade.item().asItem().getDescriptionId());
            if (!hasUpgrade(stack, upgrade)) {
                upgradeComponent.append(AAEText.UpgradeNotInstalled.text());
                upgradeComponent.withStyle(Tooltips.MUTED_COLOR);
            } else {
                upgradeComponent.withStyle(Tooltips.GREEN);
            }
            lines.add(upgradeComponent);
        }
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return true;
    }

    protected void appendExtraHoverText(
            ItemStack stack, @NotNull Level context, List<Component> lines, TooltipFlag advancedTooltips) {}

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

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        if (Addons.APOTHIC_ENCHANTING.isLoaded()
                && enchantment
                .value()
                .effects()
                .has(ApoEnchPlugin.getEnchantment(ApoEnchPlugin.Enchantment.STABLE_FOOTING))) {
            return false;
        }

        return super.canApplyAtEnchantingTable(stack, enchantment);
    }

    public boolean openFromEquipmentSlot(Player player, int inventorySlot, ItemStack stack) {
        return openFromEquipmentSlot(player, inventorySlot, stack, false);
    }

    public boolean openFromEquipmentSlot(Player player, int inventorySlot, ItemStack stack,
                                         boolean returningFromSubmenu) {
        if (!player.level().isClientSide() && checkPreconditions(stack)) {
            return MenuOpener.open(AAEMenus.QUANTUM_ARMOR_CONFIG, player, MenuLocators.forInventorySlot(inventorySlot), returningFromSubmenu);
        }
        return false;
    }

    @Override
    public <T extends LivingEntity> int damageItem(
            ItemStack stack, int amount, @Nullable T entity, Consumer<T> onBroken) {
        if (entity instanceof Player player) {
            consumeEnergy(player, stack, amount);
        }
        return 0;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null) this.renderer = new QuantumArmorRenderer();

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<GeoAnimatable>(this, 20, state -> {
            state.getController().setAnimation(RawAnimation.begin().thenLoop("animation.quantum_armor.idle"));
            Entity entity = state.getData(DataTickets.ENTITY);
            if (entity instanceof ArmorStand) return PlayState.CONTINUE;

            Set<Item> wornArmor = new ObjectOpenHashSet<>();
            for (ItemStack stack : ((Player) entity).getArmorSlots()) {
                if (stack.isEmpty()) return PlayState.STOP;

                wornArmor.add(stack.getItem());
            }

            boolean isFullSet = wornArmor.containsAll(ObjectArrayList.of(
                    AAEItems.QUANTUM_BOOTS.get(),
                    AAEItems.QUANTUM_LEGGINGS.get(),
                    AAEItems.QUANTUM_CHESTPLATE.get(),
                    AAEItems.QUANTUM_HELMET.get()));

            return isFullSet ? PlayState.CONTINUE : PlayState.STOP;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack, @Nullable BlockPos pos) {
        return new QuantumArmorMenuHost(
                player, inventorySlot, stack, (p, subMenu) -> openFromEquipmentSlot(p, inventorySlot, stack, true));
    }

    public static List<QuantumArmorBase> upgradeAvailableFor(UpgradeType type) {
        List<QuantumArmorBase> list = new ArrayList<>();

        for (var equip : AAEItems.getQuantumArmor()) {
            var armor = ((QuantumArmorBase) equip.stack().getItem());
            if (armor.possibleUpgrades.contains(type)) {
                list.add(armor);
            }
        }

        return list;
    }


}
