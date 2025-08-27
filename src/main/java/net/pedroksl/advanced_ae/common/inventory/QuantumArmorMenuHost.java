package net.pedroksl.advanced_ae.common.inventory;

import java.util.function.BiConsumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEHotkeys;
import net.pedroksl.advanced_ae.common.definitions.AAENbt;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.QuantumUpgradeBaseItem;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.api.inventories.InternalInventory;
import appeng.api.storage.ISubMenuHost;
import appeng.hooks.ticking.TickHandler;
import appeng.menu.ISubMenu;
import appeng.menu.guisync.GuiSync;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;

public class QuantumArmorMenuHost extends ItemMenuHost implements InternalInventoryHost, ISubMenuHost {

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
            Player player, int inventorySlot, ItemStack stack, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(player, inventorySlot, stack);
        this.returnToMainMenu = returnToMainMenu;

        var itemTag = this.getItemStack().getTagElement(AAENbt.STACK_TAG);
        if (itemTag != null) {
            this.input.readFromNBT(itemTag, "input");
        }
    }

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
    public void saveChanges() {
        var itemTag = new CompoundTag();
        this.input.writeToNBT(itemTag, "input");

        if (!itemTag.isEmpty()) {
            this.getItemStack().addTagElement(AAENbt.STACK_TAG, itemTag);
        } else {
            this.getItemStack().removeTagKey(AAENbt.STACK_TAG);
        }
    }

    @Override
    public void onChangeInventory(InternalInventory inv, int slot) {
        var itemTag = this.getItemStack().getOrCreateTagElement(AAENbt.STACK_TAG);
        if (this.input == inv) {
            this.input.writeToNBT(itemTag, "input");
        }

        if (!itemTag.isEmpty()) {
            this.getItemStack().addTagElement(AAENbt.STACK_TAG, itemTag);
        } else {
            this.getItemStack().removeTagKey(AAENbt.STACK_TAG);
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

    public InternalInventory getInventory() {
        return this.input;
    }

    public String getCloseHotkey() {
        return AAEHotkeys.Keys.ARMOR_CONFIG.getId();
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
        void handleChange(InternalInventory inv, int slot);
    }
}
