package net.pedroksl.advanced_ae.gui;

import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.definitions.AAESlotSemantics;
import net.pedroksl.advanced_ae.common.inventory.QuantumArmorMenuHost;
import net.pedroksl.advanced_ae.common.items.armors.IUpgradeableItem;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.QuantumUpgradeBaseItem;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.QuantumArmorUpgradeStatePacket;
import net.pedroksl.advanced_ae.network.packet.quantumarmor.QuantumArmorUpgradeTogglePacket;

import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.ISubMenuHost;
import appeng.menu.AEBaseMenu;
import appeng.menu.ISubMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.guisync.GuiSync;
import appeng.menu.interfaces.IProgressProvider;
import appeng.menu.slot.AppEngSlot;
import appeng.menu.slot.DisabledSlot;

public class QuantumArmorConfigMenu extends AEBaseMenu implements ISubMenuHost, IProgressProvider {

    @GuiSync(2)
    public int maxProcessingTime;

    @GuiSync(3)
    public int processingTime = -1;

    private final QuantumArmorMenuHost<?> host;

    private final Slot inputSlot;

    private boolean markDirty = false;

    private static final String REQUEST_UPDATE = "request_update";
    private static final String SELECT_SLOT = "select_slot";
    private static final String REQUEST_UNINSTALL = "request_uninstall";
    private static final String EMPTY_SLOT = "empty_slot";
    private static final String OPEN_STYLE = "open_style";

    public static final String LAST_SLOT_INDEX = "aae$lastSlotIndex";

    public QuantumArmorConfigMenu(int id, Inventory playerInventory, QuantumArmorMenuHost<?> host) {
        super(AAEMenus.QUANTUM_ARMOR_CONFIG.get(), id, playerInventory, host);
        this.host = host;
        this.createPlayerInventorySlots(playerInventory);

        this.inputSlot = this.addSlot(new UpgradeSlot(host.getInventory(), 0), SlotSemantics.MACHINE_INPUT);

        int indexOfFirstQuantum = -1;
        for (int i = 3; i >= 0; i--) {
            var index = Inventory.INVENTORY_SIZE + i;
            var slot = new DisabledSlot(playerInventory, index);
            if (slot.getItem().getItem() instanceof QuantumArmorBase) {
                if (indexOfFirstQuantum == -1) {
                    indexOfFirstQuantum = index;
                }
            }
            this.addSlot(slot, AAESlotSemantics.ARMOR);
        }

        maxProcessingTime = this.host.getMaxProcessingTime();
        this.host.setProgressChangedHandler(this::progressChanged);
        this.host.setUpgradeAppliedWatcher(() -> this.markDirty = true);
        this.host.setInventoryChangedHandler(this::onChangeInventory);

        registerClientAction(REQUEST_UPDATE, this::updateClient);
        registerClientAction(SELECT_SLOT, Integer.class, this::setSelectedItemSlot);
        registerClientAction(REQUEST_UNINSTALL, UpgradeType.class, this::requestUninstall);
        registerClientAction(EMPTY_SLOT, this::emptyUpgradeSlot);
        registerClientAction(OPEN_STYLE, this::openStyleScreen);

        if (getPlayer().getPersistentData().contains(LAST_SLOT_INDEX)) {
            setSelectedItemSlot(getPlayer().getPersistentData().getInt(LAST_SLOT_INDEX));
        } else {
            setSelectedItemSlot(indexOfFirstQuantum);
        }
    }

    public QuantumArmorMenuHost<?> getHost() {
        return this.host;
    }

    private void progressChanged(int progress) {
        this.processingTime = progress;
    }

    private void onChangeInventory(InternalInventory inv, int slot) {}

    @Override
    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        this.host.returnToMainMenu(player, iSubMenu);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return this.host.getItemStack();
    }

    @Override
    public int getCurrentProgress() {
        return this.processingTime;
    }

    @Override
    public int getMaxProgress() {
        return this.maxProcessingTime;
    }

    public boolean isArmorSlot(Slot slot) {
        return this.getSlots(AAESlotSemantics.ARMOR).contains(slot);
    }

    public void setSelectedItemSlot(int index) {
        if (isClientSide()) {
            sendClientAction(SELECT_SLOT, index);
            getPlayer().getPersistentData().putInt(LAST_SLOT_INDEX, index);
            return;
        }

        this.host.setSelectedItemSlot(index);
        this.markDirty = true;
    }

    public int getSelectedSlotIndex() {
        return this.host.selectedItemSlot;
    }

    public void toggleUpgradeEnable(UpgradeType upgradeType, boolean state) {
        if (isClientSide()) {
            PacketDistributor.sendToServer(new QuantumArmorUpgradeTogglePacket(upgradeType, state));
            return;
        }

        var slotIndex = this.host.selectedItemSlot;
        var stack = getPlayer().getInventory().getItem(slotIndex);
        if (stack.getItem() instanceof QuantumArmorBase item) {
            if (item.getPossibleUpgrades().contains(upgradeType)) {
                if (item.hasUpgrade(stack, upgradeType)) {
                    item.toggleUpgrade(stack, upgradeType);
                    this.markDirty = true;
                }
            }
        }
    }

    public void openNumInputConfigScreen(UpgradeType upgradeType, int currentValue) {
        var locator = getLocator();
        if (locator != null) {
            QuantumArmorNumInputConfigMenu.open(
                    ((ServerPlayer) this.getPlayer()),
                    getLocator(),
                    this.getSelectedSlotIndex(),
                    upgradeType,
                    currentValue);
        }
    }

    public void openFilterConfigScreen(UpgradeType upgradeType, List<GenericStack> filter) {
        var locator = getLocator();
        if (locator != null && isServerSide()) {
            QuantumArmorFilterConfigMenu.open(
                    ((ServerPlayer) this.getPlayer()), getLocator(), this.getSelectedSlotIndex(), filter, upgradeType);
        }
    }

    public void openMagnetScreen(int currentValue, List<GenericStack> filter, boolean blacklist) {
        var locator = getLocator();
        if (locator != null && isServerSide()) {
            QuantumArmorMagnetMenu.open(
                    ((ServerPlayer) this.getPlayer()),
                    getLocator(),
                    this.getSelectedSlotIndex(),
                    filter,
                    currentValue,
                    blacklist);
        }
    }

    public void openStyleScreen() {
        if (isClientSide()) {
            sendClientAction(OPEN_STYLE);
            return;
        }

        var locator = getLocator();
        if (locator != null) {
            QuantumArmorStyleConfigMenu.open(
                    ((ServerPlayer) this.getPlayer()), getLocator(), this.getSelectedSlotIndex());
        }
    }

    public void requestUninstall(UpgradeType upgradeType) {
        if (isClientSide()) {
            sendClientAction(REQUEST_UNINSTALL, upgradeType);
            return;
        }

        boolean upgradeRemoved = false;
        var slotIndex = this.host.selectedItemSlot;
        var stack = getPlayer().getInventory().getItem(slotIndex);
        if (stack.getItem() instanceof QuantumArmorBase item) {
            if (item.getPossibleUpgrades().contains(upgradeType)) {
                if (item.hasUpgrade(stack, upgradeType)) {
                    upgradeRemoved = item.removeUpgrade(stack, upgradeType);
                }
            }
        }

        var upgradeStack = upgradeType.item().stack();
        if (upgradeRemoved) {
            if (!getPlayer().getInventory().add(upgradeStack)) {
                getPlayer().drop(upgradeStack, false);
            }
        }
        this.markDirty = true;
    }

    public void updateClient() {
        if (isClientSide()) {
            sendClientAction(REQUEST_UPDATE);
            return;
        }

        var slotIndex = this.host.selectedItemSlot;
        var stack = getPlayer().getInventory().getItem(slotIndex);
        if (!stack.isEmpty() && stack.getItem() instanceof IUpgradeableItem item) {
            var index = 4 - (slotIndex - Inventory.INVENTORY_SIZE) + Inventory.INVENTORY_SIZE;
            var states = item.getAllUpgradeStates(stack);
            sendPacketToClient(new QuantumArmorUpgradeStatePacket(index, states));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        if (isServerSide() && markDirty) {
            updateClient();
            markDirty = false;
        }
    }

    public void emptyUpgradeSlot() {
        if (isClientSide()) {
            sendClientAction(EMPTY_SLOT);
            return;
        }

        if (!this.inputSlot.getItem().isEmpty()) {
            this.getPlayer().addItem(this.inputSlot.getItem());
            this.inputSlot.set(ItemStack.EMPTY);
        }
    }

    private static class UpgradeSlot extends AppEngSlot {
        public UpgradeSlot(InternalInventory inv, int invSlot) {
            super(inv, invSlot);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if (stack.getItem() instanceof QuantumUpgradeBaseItem upgrade) {
                return upgrade.getType() != UpgradeType.EMPTY;
            }
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }
    }
}
