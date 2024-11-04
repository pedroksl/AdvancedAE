package net.pedroksl.advanced_ae.common.items.armors;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.helpers.PickCraftMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.MenuSelectionPacket;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.ISubMenuHost;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.core.localization.Tooltips;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.menu.me.crafting.CraftAmountMenu;

import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;

public class QuantumChestplate extends QuantumArmorBase implements GeoItem, ISubMenuHost {

    private static final double MAX_POWER_STORAGE = 300000000;

    public QuantumChestplate(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.CHESTPLATE, properties, () -> MAX_POWER_STORAGE);

        registerUpgrades(
                UpgradeType.FLIGHT,
                UpgradeType.HP_BUFFER,
                UpgradeType.LAVA_IMMUNITY,
                UpgradeType.REGENERATION,
                UpgradeType.STRENGTH,
                UpgradeType.ATTACK_SPEED,
                UpgradeType.CHARGING,
                UpgradeType.PICK_CRAFT);
    }

    @Override
    protected void appendExtraHoverText(
            ItemStack stack, TooltipContext context, List<Component> lines, TooltipFlag advancedTooltips) {
        lines.add(AAEText.QuantumArmorStableFootingTooltip.text().withStyle(Tooltips.NUMBER_TEXT));
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (slotId == Inventory.INVENTORY_SIZE + EquipmentSlot.CHEST.getIndex()) {
            if (entity instanceof Player player) {
                if (!getPassiveUpgrades(stack).isEmpty()) {
                    tickUpgrades(level, player, stack);
                }

                toggleBoneVisibilities(stack, player);
            }
        }
    }

    private void toggleBoneVisibilities(ItemStack stack, Player player) {
        var item = (QuantumArmorBase) stack.getItem();
        var renderProvider = item.getRenderProvider();
        if (renderProvider instanceof GeoRenderProvider provider) {
            var renderer = provider.getGeoArmorRenderer(player, stack, EquipmentSlot.CHEST, null);
            if (renderer instanceof QuantumArmorRenderer quantumRenderer) {
                var visible = stack.has(AAEComponents.UPGRADE_TOGGLE.get(UpgradeType.STRENGTH));
                quantumRenderer.setBoneVisible(QuantumArmorRenderer.LEFT_BLADE_BONE, visible);
                quantumRenderer.setBoneVisible(QuantumArmorRenderer.RIGHT_BLADE_BONE, visible);
            }
        }
    }

    @Override
    public boolean openFromEquipmentSlot(Player player, ItemMenuHostLocator locator, boolean returningFromSubmenu) {
        var is = locator.locateItem(player);
        if (!player.level().isClientSide() && checkPreconditions(is)) {
            player.getPersistentData().putInt(MENU_TYPE, MenuId.STANDARD.id);
            PacketDistributor.sendToPlayer(
                    ((ServerPlayer) player), new MenuSelectionPacket(MENU_TYPE, MenuId.STANDARD.id));
        }
        return super.openFromEquipmentSlot(player, locator, returningFromSubmenu);
    }

    public boolean attemptCraftingTarget(Player player, ItemMenuHostLocator locator) {
        var is = locator.locateItem(player);

        if (!player.level().isClientSide() && checkPreconditions(is)) {
            var chestStack = (QuantumChestplate) is.getItem();
            var upgrade = UpgradeType.PICK_CRAFT;
            if (chestStack.hasUpgrade(is, upgrade)) {
                if (chestStack.isUpgradeEnabled(is, upgrade)) {
                    var key = targetKey(player);
                    if (key != null) {
                        if (keyIsCraftable(player, is, key)) {
                            player.getPersistentData().putInt(MENU_TYPE, MenuId.CRAFTING.id);
                            PacketDistributor.sendToPlayer(
                                    ((ServerPlayer) player), new MenuSelectionPacket(MENU_TYPE, MenuId.CRAFTING.id));
                            CraftAmountMenu.open(((ServerPlayer) player), locator, key, 1);
                        }
                        // Item is not craftable
                        else {
                            player.displayClientMessage(AAEText.ItemNotCraftable.text(), true);
                        }
                    }
                    // No available target
                    else {
                        player.displayClientMessage(AAEText.NoAvailableTarget.text(), true);
                    }
                }
                // Upgrade disabled
                else {
                    var id = Component.translatable(upgrade.item().asItem().getDescriptionId());
                    player.displayClientMessage(AAEText.UpgradeNotEnabledMessage.text(id), true);
                }
            }
            // Upgrade disabled
            else {
                var id = Component.translatable(upgrade.item().asItem().getDescriptionId());
                player.displayClientMessage(AAEText.UpgradeNotInstalledMessage.text(id), true);
            }
        }
        return false;
    }

    @Override
    public ItemMenuHost<?> getMenuHost(Player player, ItemMenuHostLocator locator, @Nullable BlockHitResult hitResult) {
        if (player.getPersistentData().contains(MENU_TYPE)
                && player.getPersistentData().getInt(MENU_TYPE) == MenuId.STANDARD.id) {
            player.getPersistentData().remove(MENU_TYPE);
            return super.getMenuHost(player, locator, hitResult);
        }
        player.getPersistentData().remove(MENU_TYPE);
        return new PickCraftMenuHost<>(this, player, locator);
    }

    private AEKey targetKey(Player player) {
        var hitResult = Minecraft.getInstance().hitResult;
        if (hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() == HitResult.Type.BLOCK) {
            var blockPos = blockHitResult.getBlockPos();
            var blockState = player.level().getBlockState(blockPos);
            ItemStack itemStack = blockState.getBlock().asItem().getDefaultInstance();
            if (blockState.hasBlockEntity()) {
                BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
                if (blockEntity != null) {
                    if (blockEntity instanceof CableBusBlockEntity cable) {
                        Direction dir = player.getNearestViewDirection();
                        var part = cable.getPart(dir.getOpposite());
                        if (part != null) {
                            itemStack = new ItemStack(part.getPartItem().asItem(), 1);
                        }
                    } else {
                        blockEntity.saveToItem(itemStack, player.registryAccess());
                    }
                }
            }
            return AEItemKey.of(itemStack.getItem().getDefaultInstance());
        }
        return null;
    }

    public boolean keyIsCraftable(Player player, ItemStack stack, AEKey whatToCraft) {
        MutableObject<Component> errorHolder = new MutableObject<>();
        var grid = this.getLinkedGrid(stack, player.level(), errorHolder::setValue);
        if (grid != null) {
            return grid.getCraftingService().isCraftable(whatToCraft);
        }
        // Grid unavailable
        else {
            player.displayClientMessage(errorHolder.getValue(), true);
        }
        return false;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {}

    @Override
    public ItemStack getMainMenuIcon() {
        return AAEItems.QUANTUM_CHESTPLATE.stack();
    }
}
