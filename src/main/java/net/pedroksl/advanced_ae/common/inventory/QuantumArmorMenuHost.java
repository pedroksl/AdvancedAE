package net.pedroksl.advanced_ae.common.inventory;

import java.util.function.BiConsumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEHotkeysRegistry;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.QuantumUpgradeBaseItem;
import net.pedroksl.ae2addonlib.registry.helpers.LibComponents;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.storage.ISubMenuHost;
import appeng.hooks.ticking.TickHandler;
import appeng.menu.ISubMenu;
import appeng.menu.guisync.GuiSync;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

public class QuantumArmorMenuHost<T extends QuantumArmorBase> extends ItemMenuHost<T>
        implements InternalInventoryHost, ISubMenuHost {

    private static final int MAX_PROCESSING_TIME = 10;

    private final AppEngInternalInventory input = new AppEngInternalInventory(this, 1, 1);
    private ProgressChangedHandler progressChangedHandler;
    private InventoryChangedHandler invChangeHandler;
    private ClientUpdater clientUpdater;

    private final BiConsumer<Player, ISubMenu> returnToMainMenu;

    private long consumeCardStartTick = -1;

    @GuiSync(10)
    private int selectedItemSlot = -1;

    public QuantumArmorMenuHost(
            T item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator);
        this.returnToMainMenu = returnToMainMenu;

        var itemTag = this.getItemStack().get(LibComponents.NBT_TAG);
        var registry = player.registryAccess();
        if (itemTag != null) {
            this.input.readFromNBT(itemTag, "input", registry);
        }
    }

    @Override
    public void tick() {
        var inputCard = this.input.getStackInSlot(0);
        if (inputCard.isEmpty() || selectedItemSlot == -1) {
            resetProgress();
            return;
        }

        ItemStack stack = super.getPlayer().getInventory().getItem(this.selectedItemSlot);
        if (!(stack.getItem() instanceof QuantumArmorBase item)) {
            resetProgress();
            return;
        }

        QuantumUpgradeBaseItem card = ((QuantumUpgradeBaseItem) inputCard.getItem());
        if (!item.isUpgradeAllowed(card.getType()) || item.hasUpgrade(stack, card.getType())) {
            resetProgress();
            return;
        }

        var currentTick = TickHandler.instance().getCurrentTick();
        if (consumeCardStartTick == -1) {
            consumeCardStartTick = currentTick;
        }

        int progressTime = (int) (currentTick - consumeCardStartTick);
        if (progressTime >= MAX_PROCESSING_TIME) {
            progressTime = -1;
            updateProgress(progressTime);

            var type = card.getType();
            if (item.applyUpgrade(stack, type)) {
                this.input.setItemDirect(0, ItemStack.EMPTY);
                if (clientUpdater != null) {
                    clientUpdater.updateClient();
                }
            }
        } else {
            updateProgress(progressTime);
        }
    }

    public void setSelectedItemSlot(int slot) {
        resetProgress();
        selectedItemSlot = slot;
    }

    public int getSelectedSlotIndex() {
        return selectedItemSlot;
    }

    private void resetProgress() {
        if (consumeCardStartTick != -1) {
            consumeCardStartTick = -1;
            updateProgress(-1);
        }
    }

    public int getMaxProcessingTime() {
        return MAX_PROCESSING_TIME;
    }

    private void updateProgress(int value) {
        if (progressChangedHandler != null) {
            progressChangedHandler.handleProgress(value);
        }
    }

    @Override
    public void saveChangedInventory(AppEngInternalInventory appEngInternalInventory) {
        var itemTag = new CompoundTag();
        var registry = this.getPlayer().registryAccess();
        this.input.writeToNBT(itemTag, "input", registry);

        if (!itemTag.isEmpty()) {
            this.getItemStack().set(LibComponents.NBT_TAG, itemTag);
        } else {
            this.getItemStack().remove(LibComponents.NBT_TAG);
        }
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        var itemTag = this.getItemStack().getOrDefault(LibComponents.NBT_TAG, new CompoundTag());
        var registry = this.getPlayer().registryAccess();
        if (this.input == inv) {
            this.input.writeToNBT(itemTag, "input", registry);
        }

        if (!itemTag.isEmpty()) {
            this.getItemStack().set(LibComponents.NBT_TAG, itemTag);
        } else {
            this.getItemStack().remove(LibComponents.NBT_TAG);
        }

        if (invChangeHandler != null) {
            invChangeHandler.handleChange(inv, slot);
        }
    }

    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        this.returnToMainMenu.accept(player, iSubMenu);
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return this.getItemStack();
    }

    public void setProgressChangedHandler(ProgressChangedHandler handler) {
        progressChangedHandler = handler;
    }

    public void setUpgradeAppliedWatcher(ClientUpdater handler) {
        clientUpdater = handler;
    }

    public void setInventoryChangedHandler(InventoryChangedHandler handler) {
        invChangeHandler = handler;
    }

    public AppEngInternalInventory getInventory() {
        return this.input;
    }

    public String getCloseHotkey() {
        return AAEHotkeysRegistry.Keys.ARMOR_CONFIG.getId();
    }

    @FunctionalInterface
    public interface ProgressChangedHandler {
        void handleProgress(int progressTime);
    }

    @FunctionalInterface
    public interface ClientUpdater {
        void updateClient();
    }

    @FunctionalInterface
    public interface InventoryChangedHandler {
        void handleChange(AppEngInternalInventory inv, int slot);
    }
}
