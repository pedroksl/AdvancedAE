package net.pedroksl.advanced_ae.gui;

import java.util.LinkedHashMap;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.inventory.AdvPatternEncoderHost;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsEncoder;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderPacket;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.slot.OutputSlot;
import appeng.menu.slot.RestrictedInputSlot;

public class AdvPatternEncoderMenu extends AEBaseMenu {
    private final RestrictedInputSlot inputSlot;
    private final OutputSlot outputSlot;
    private final AdvPatternEncoderHost host;

    public AdvPatternEncoderMenu(int id, Inventory playerInventory, AdvPatternEncoderHost host) {
        super(AAEMenus.ADV_PATTERN_ENCODER.get(), id, playerInventory, host);
        this.createPlayerInventorySlots(playerInventory);
        this.host = host;

        this.addSlot(
                this.inputSlot = new RestrictedInputSlot(
                        RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN, host.getInventory(), 0),
                SlotSemantics.ENCODED_PATTERN);
        this.addSlot(this.outputSlot = new OutputSlot(host.getInventory(), 1, null), SlotSemantics.MACHINE_OUTPUT);

        this.host.setInventoryChangedHandler(this::onChangeInventory);
        if (this.inputSlot.hasItem()) {
            decodeInputPattern();
        }
    }

    public void onUpdateRequested() {
        if (this.inputSlot.hasItem()) {
            decodeInputPattern();
        }
    }

    public void onChangeInventory(InternalInventory inv, int slot) {
        if (inv == host.getInventory()) {
            if (slot == 0) {
                if (this.inputSlot.hasItem()) {
                    decodeInputPattern();
                } else if (!this.inputSlot.hasItem()) {
                    clearDecodedPattern();
                    this.outputSlot.set(ItemStack.EMPTY);
                }
            } else if (slot == 1 && !this.outputSlot.hasItem()) {
                clearDecodedPattern();
                this.inputSlot.set(ItemStack.EMPTY);
            }
        }
    }

    private void decodeInputPattern() {
        ItemStack stack = this.inputSlot.getItem();

        IPatternDetails details =
                PatternDetailsHelper.decodePattern(stack, this.getPlayer().level());

        if (details == null) return;

        LinkedHashMap<AEKey, Direction> dirMap = new LinkedHashMap<>();
        if (details instanceof AEProcessingPattern processingPattern) {
            dirMap = decodeProcessingPattern(processingPattern);
        } else if (details instanceof AdvProcessingPattern advProcessingPattern) {
            dirMap = decodeAdvProcessingPattern(advProcessingPattern);
        }

        if (this.getPlayer() instanceof ServerPlayer sp) {
            PacketDistributor.sendToPlayer(sp, new AdvPatternEncoderPacket(dirMap));
        }
    }

    private LinkedHashMap<AEKey, Direction> decodeProcessingPattern(AEProcessingPattern pattern) {
        var sparseInputs = pattern.getSparseInputs();

        LinkedHashMap<AEKey, Direction> inputMap = new LinkedHashMap<>();
        for (GenericStack input : sparseInputs) {
            if (input == null) {
                continue;
            }

            if (!inputMap.containsKey(input.what())) {
                inputMap.put(input.what(), null);
            }
        }

        var newAdvPattern = AdvPatternDetailsEncoder.encodeProcessingPattern(
                pattern.getSparseInputs(), pattern.getSparseOutputs(), inputMap);
        this.outputSlot.set(newAdvPattern);

        return inputMap;
    }

    private LinkedHashMap<AEKey, Direction> decodeAdvProcessingPattern(AdvProcessingPattern pattern) {
        this.outputSlot.set(this.inputSlot.getItem().copy());

        return pattern.getDirectionMap();
    }

    private void clearDecodedPattern() {
        if (this.getPlayer() instanceof ServerPlayer sp) {
            PacketDistributor.sendToPlayer(sp, new AdvPatternEncoderPacket(new LinkedHashMap<>()));
        }
    }

    public void update(AEKey key, Direction dir) {
        if (!this.outputSlot.hasItem()) {
            copyItemToOutputSlot();
        }

        AdvProcessingPattern details = (AdvProcessingPattern) PatternDetailsHelper.decodePattern(
                this.outputSlot.getItem(), this.getPlayer().level());
        if (details != null) {
            var dirMap = details.getDirectionMap();
            dirMap.put(key, dir);
            var newPattern = AdvPatternDetailsEncoder.encodeProcessingPattern(
                    details.getSparseInputs(), details.getSparseOutputs(), dirMap);
            this.outputSlot.set(newPattern);

            if (this.getPlayer() instanceof ServerPlayer sp) {
                PacketDistributor.sendToPlayer(sp, new AdvPatternEncoderPacket(dirMap));
            }
        }
    }

    public void copyItemToOutputSlot() {
        ItemStack stack = this.inputSlot.getItem();

        IPatternDetails details =
                PatternDetailsHelper.decodePattern(stack, this.getPlayer().level());

        if (details == null) return;

        if (details instanceof AEProcessingPattern processingPattern) {
            decodeProcessingPattern(processingPattern);
        } else if (details instanceof AdvProcessingPattern advProcessingPattern) {
            decodeAdvProcessingPattern(advProcessingPattern);
        }
    }

    public AdvPatternEncoderHost getHost() {
        return host;
    }

    public interface InventoryChangedHandler {
        void handleChange(InternalInventory inv, int slot);
    }
}
