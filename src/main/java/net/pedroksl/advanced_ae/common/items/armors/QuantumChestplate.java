package net.pedroksl.advanced_ae.common.items.armors;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.parts.SelectedPart;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.storage.ISubMenuHost;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.core.localization.Tooltips;
import appeng.menu.ISubMenu;
import appeng.menu.locator.MenuLocators;
import appeng.menu.me.crafting.CraftAmountMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.pedroksl.advanced_ae.client.renderer.QuantumArmorRenderer;
import net.pedroksl.advanced_ae.common.definitions.AAEItems;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;
import net.pedroksl.advanced_ae.common.definitions.AAEText;
import net.pedroksl.advanced_ae.common.helpers.PickCraftMenuHost;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.MenuSelectionPacket;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.List;

public class QuantumChestplate extends QuantumArmorBase implements GeoItem, ISubMenuHost {

    private static final double MAX_POWER_STORAGE = 300000000;

    public QuantumChestplate(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY, Type.CHESTPLATE, properties, () -> MAX_POWER_STORAGE);

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
            ItemStack stack, @NotNull Level context, List<Component> lines, TooltipFlag advancedTooltips) {
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
        var renderer = IClientItemExtensions.of(item).getHumanoidArmorModel(player, stack, EquipmentSlot.CHEST, null);
        if (renderer instanceof QuantumArmorRenderer quantumRenderer) {
            var visible = item.hasUpgrade(stack, UpgradeType.STRENGTH);
            quantumRenderer.setBoneVisible(QuantumArmorRenderer.LEFT_BLADE_BONE, visible);
            quantumRenderer.setBoneVisible(QuantumArmorRenderer.RIGHT_BLADE_BONE, visible);
        }
    }

    @Override
    public boolean openFromEquipmentSlot(Player player, int inventorySlot, ItemStack stack,
                                         boolean returningFromSubmenu) {
        if (player instanceof ServerPlayer serverPlayer && checkPreconditions(stack)) {
            player.getPersistentData().putInt(MENU_TYPE, MenuId.STANDARD.id);
            AAENetworkHandler.INSTANCE.sendTo(new MenuSelectionPacket(MENU_TYPE, MenuId.STANDARD.id), serverPlayer);
        }
        return super.openFromEquipmentSlot(player, inventorySlot, stack, returningFromSubmenu);
    }

    public boolean attemptCraftingTarget(Player player, int inventorySlot, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer && checkPreconditions(stack)) {
            var chestStack = (QuantumChestplate) stack.getItem();
            var upgrade = UpgradeType.PICK_CRAFT;
            if (chestStack.hasUpgrade(stack, upgrade)) {
                if (chestStack.isUpgradeEnabled(stack, upgrade)) {
                    var key = targetKey(player);
                    if (key != null) {
                        if (keyIsCraftable(player, stack, key)) {
                            player.getPersistentData().putInt(MENU_TYPE, MenuId.CRAFTING.id);
                            AAENetworkHandler.INSTANCE.sendTo(new MenuSelectionPacket(MENU_TYPE, MenuId.CRAFTING.id),
                                    serverPlayer);
                            CraftAmountMenu.open(serverPlayer, MenuLocators.forInventorySlot(inventorySlot), key, 1);
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
    public ItemMenuHost getMenuHost(Player player, int inventorySlot, ItemStack stack,
                                       @Nullable BlockPos pos) {
        if (player.getPersistentData().contains(MENU_TYPE)
                && player.getPersistentData().getInt(MENU_TYPE) == MenuId.STANDARD.id) {
            player.getPersistentData().remove(MENU_TYPE);
            return super.getMenuHost(player, inventorySlot, stack, pos);
        }
        player.getPersistentData().remove(MENU_TYPE);
        return new PickCraftMenuHost<>(player, inventorySlot, stack);
    }

    private AEKey targetKey(Player player) {
        var hitResult = player.pick(player.getBlockReach(), 0f, false);
        if (hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() == HitResult.Type.BLOCK) {
            var blockPos = blockHitResult.getBlockPos();
            var blockState = player.level().getBlockState(blockPos);
            ItemStack itemStack = blockState.getBlock().asItem().getDefaultInstance();
            if (blockState.hasBlockEntity()) {
                BlockEntity blockEntity = player.level().getBlockEntity(blockPos);
                if (blockEntity != null) {
                    if (blockEntity instanceof CableBusBlockEntity cable) {
                        var part = getSelectedPart(cable, hitResult);
                        if (part != null && part.part != null) {
                            itemStack = new ItemStack(part.part.getPartItem().asItem(), 1);
                        }

                    } else {
                        blockEntity.saveToItem(itemStack);
                    }
                }
            }
            return AEItemKey.of(itemStack.getItem().getDefaultInstance());
        }
        return null;
    }

    private static SelectedPart getSelectedPart(CableBusBlockEntity cable, HitResult hitResult) {
        var loc = hitResult.getLocation();
        var x = loc.x - (int) loc.x;
        if (x < 0) x++;
        var y = loc.y - (int) loc.y;
        if (y < 0) y++;
        var z = loc.z - (int) loc.z;
        if (z < 0) z++;
        var vec = new Vec3(x, y, z);
        return cable.selectPartLocal(vec);
    }

    public boolean keyIsCraftable(Player player, ItemStack stack, AEKey whatToCraft) {
        MutableObject<Component> errorHolder = new MutableObject<>();
        var grid = this.getLinkedGrid(stack, player.level(), player);
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
