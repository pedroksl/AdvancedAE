package net.pedroksl.advanced_ae.gui.patternencoder;

import appeng.api.crafting.IPatternDetails;
import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.inventories.InternalInventory;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.crafting.pattern.AEProcessingPattern;
import appeng.menu.AEBaseMenu;
import appeng.menu.SlotSemantics;
import appeng.menu.implementations.MenuTypeBuilder;
import appeng.menu.slot.OutputSlot;
import appeng.menu.slot.RestrictedInputSlot;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.common.AAEItemAndBlock;
import net.pedroksl.advanced_ae.common.inventory.AdvPatternEncoderInventory;
import net.pedroksl.advanced_ae.common.patterns.AdvPatternDetailsEncoder;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPattern;
import net.pedroksl.advanced_ae.common.patterns.AdvProcessingPatternItem;
import net.pedroksl.advanced_ae.network.AAENetworkHandler;
import net.pedroksl.advanced_ae.network.packet.AdvPatternEncoderPacket;

import java.util.HashMap;

public class AdvPatternEncoderContainer extends AEBaseMenu {
	public static final MenuType<AdvPatternEncoderContainer> TYPE = MenuTypeBuilder
			.create(AdvPatternEncoderContainer::new, AdvPatternEncoderInventory.class)
			.build("adv_pattern_encoder");

	private final RestrictedInputSlot inputSlot;
	private final OutputSlot outputSlot;
	private final AdvPatternEncoderInventory host;

	public AdvPatternEncoderContainer(int id, Inventory playerInventory, AdvPatternEncoderInventory host) {
		super(TYPE, id, playerInventory, host);
		this.createPlayerInventorySlots(playerInventory);
		this.host = host;

		this.addSlot(this.inputSlot = new RestrictedInputSlot(RestrictedInputSlot.PlacableItemType.ENCODED_PATTERN,
				host.getInventory(), 0), SlotSemantics.ENCODED_PATTERN);
		this.addSlot(this.outputSlot = new OutputSlot(host.getInventory(), 1, null), SlotSemantics.MACHINE_OUTPUT);

		this.host.setInventoryChangedHandler(this::onChangeInventory);
		if (this.inputSlot.hasItem()) {
			decodeInputPattern();
		}
	}

	public void onChangeInventory(InternalInventory inv, int slot) {
		if (inv == host.getInventory()) {
			if (slot == 0) {
				if (this.inputSlot.hasItem() && !this.outputSlot.hasItem()) {
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

		IPatternDetails details = PatternDetailsHelper.decodePattern(stack, this.getPlayer().level(), false);

		if (details == null) return;

		if (!(details instanceof AEProcessingPattern pattern)) return;
		boolean advPattern = details instanceof AdvProcessingPattern;
		AdvProcessingPattern advDetails = advPattern ? (AdvProcessingPattern) details : null;

		var sparseInputs = pattern.getSparseInputs();

		HashMap<AEKey, Direction> inputList = new HashMap<>();
		for (GenericStack input : sparseInputs) {
			if (input == null) {
				continue;
			}

			if (!inputList.containsKey(input.what())) {
				Direction dir = advPattern ? advDetails.getDirectionSideForInputKey(input.what()) : null;
				inputList.put(input.what(), dir);
			}
		}

		if (advPattern) {
			this.outputSlot.set(stack.copy());
		} else {
			var newAdvPattern = AdvPatternDetailsEncoder.encodeProcessingPattern(pattern.getSparseInputs(),
					pattern.getSparseOutputs(), inputList);
			this.outputSlot.set(newAdvPattern);
		}

		if (this.getPlayer() instanceof ServerPlayer sp) {
			AAENetworkHandler.INSTANCE.sendTo(new AdvPatternEncoderPacket(inputList), sp);
		}
	}

	private void clearDecodedPattern() {
		if (this.getPlayer() instanceof ServerPlayer sp) {
			AAENetworkHandler.INSTANCE.sendTo(new AdvPatternEncoderPacket(), sp);
		}
	}

	public void update(AEKey key, Direction dir) {
		if (!this.outputSlot.hasItem()) {
			copyItemToOutputSlot();
		}

		var item = this.outputSlot.getItem().getItem();
		if (item instanceof AdvProcessingPatternItem patternItem) {
			AdvProcessingPattern pattern = patternItem.decode(this.outputSlot.getItem(), this.getPlayer().level(), false);
			if (pattern != null) {
				var dirMap = pattern.getDirectionMap();
				dirMap.put(key, dir);
				var newPattern = AdvPatternDetailsEncoder.encodeProcessingPattern(pattern.getSparseInputs(),
						pattern.getSparseOutputs(), dirMap);
				this.outputSlot.set(newPattern);
			}
		}
	}

	public void copyItemToOutputSlot() {
		ItemStack stack = this.inputSlot.getItem();

		IPatternDetails details = PatternDetailsHelper.decodePattern(stack, this.getPlayer().level(), false);

		if (details == null) return;

		if (!(details instanceof AEProcessingPattern pattern)) return;
		boolean advPattern = details instanceof AdvProcessingPattern;

		if (advPattern) {
			this.outputSlot.set(stack);
		} else {
			var newAdvPattern = AAEItemAndBlock.ADV_PROCESSING_PATTERN.encode(pattern.getSparseInputs(),
					pattern.getSparseOutputs());
			this.outputSlot.set(newAdvPattern);
		}
	}

	public interface inventoryChangedHandler {
		void handleChange(InternalInventory inv, int slot);
	}
}
