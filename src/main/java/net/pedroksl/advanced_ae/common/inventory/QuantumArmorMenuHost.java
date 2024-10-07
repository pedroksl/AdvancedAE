package net.pedroksl.advanced_ae.common.inventory;

import java.util.function.BiConsumer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

import appeng.api.implementations.menuobjects.ItemMenuHost;
import appeng.hooks.ticking.TickHandler;
import appeng.menu.ISubMenu;
import appeng.menu.locator.ItemMenuHostLocator;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import net.pedroksl.advanced_ae.common.items.upgrades.QuantumUpgradeBaseItem;

public class QuantumArmorMenuHost<T extends QuantumArmorBase> extends ItemMenuHost<T> implements InternalInventoryHost {

    private static final int MAX_PROCESSING_TIME = 20;

    private final AppEngInternalInventory input = new AppEngInternalInventory(this, 1, 1);
    private ProgressChangedHandler progressChangedHandler;
    private InventoryChangedHandler invChangeHandler;

    private final BiConsumer<Player, ISubMenu> returnToMainMenu;

    private long consumeCardStartTick = -1;

    private int selectedItemSlot = -1;

    public QuantumArmorMenuHost(
            T item, Player player, ItemMenuHostLocator locator, BiConsumer<Player, ISubMenu> returnToMainMenu) {
        super(item, player, locator);
        this.returnToMainMenu = returnToMainMenu;
    }

    @Override
    public void tick() {
        var inputCard = this.input.getStackInSlot(0);
        if (inputCard.isEmpty() || selectedItemSlot == -1) {
            if (consumeCardStartTick != -1) {
                consumeCardStartTick = -1;
                updateProgress(-1);
            }
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

            // Apply the upgrade
            ItemStack stack = super.getPlayer().getInventory().getItem(this.selectedItemSlot);
            if (stack.getItem() instanceof QuantumArmorBase item) {
                QuantumUpgradeBaseItem card = ((QuantumUpgradeBaseItem) inputCard.getItem());
                if (item.isUpgradeAllowed(card.getType())) {
                    var type = card.getType();
                    stack.set(type.getComponent(), type.getDefaultValue());
                }
            }

            // Consume the upgrade card
            this.input.setItemDirect(0, ItemStack.EMPTY);
        } else {
            updateProgress(progressTime);
        }
    }

    public void setSelectedItemSlot(int slot) {
        selectedItemSlot = slot;
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
            this.getItemStack().set(AAEComponents.STACK_TAG, itemTag);
        } else {
            this.getItemStack().remove(AAEComponents.STACK_TAG);
        }
    }

    @Override
    public void onChangeInventory(AppEngInternalInventory inv, int slot) {
        var itemTag = this.getItemStack().getOrDefault(AAEComponents.STACK_TAG, new CompoundTag());
        var registry = this.getPlayer().registryAccess();
        if (this.input == inv) {
            this.input.writeToNBT(itemTag, "input", registry);
        }

        if (!itemTag.isEmpty()) {
            this.getItemStack().set(AAEComponents.STACK_TAG, itemTag);
        } else {
            this.getItemStack().remove(AAEComponents.STACK_TAG);
        }

        if (invChangeHandler != null) {
            invChangeHandler.handleChange(inv, slot);
        }
    }

    public void returnToMainMenu(Player player, ISubMenu iSubMenu) {
        this.returnToMainMenu.accept(player, iSubMenu);
    }

    public void setProgressChangedHandler(ProgressChangedHandler handler) {
        progressChangedHandler = handler;
    }

    public void setInventoryChangedHandler(InventoryChangedHandler handler) {
        invChangeHandler = handler;
    }

    public AppEngInternalInventory getInventory() {
        return this.input;
    }

    @FunctionalInterface
    public interface ProgressChangedHandler {
        void handleProgress(int progressTime);
    }

    @FunctionalInterface
    public interface InventoryChangedHandler {
        void handleChange(AppEngInternalInventory inv, int slot);
    }
}
