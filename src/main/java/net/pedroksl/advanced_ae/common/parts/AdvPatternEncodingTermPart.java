package net.pedroksl.advanced_ae.common.parts;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.helpers.IPatternTerminalLogicHost;
import appeng.helpers.IPatternTerminalMenuHost;
import appeng.menu.me.items.PatternEncodingTermMenu;
import appeng.parts.PartModel;
import appeng.parts.encoding.PatternEncodingLogic;
import appeng.parts.reporting.AbstractTerminalPart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.pedroksl.advanced_ae.AdvancedAE;

import java.util.Arrays;
import java.util.List;

public class AdvPatternEncodingTermPart extends AbstractTerminalPart
		implements IPatternTerminalLogicHost, IPatternTerminalMenuHost {

	public static List<ResourceLocation> MODELS = Arrays.asList(
			new ResourceLocation(AdvancedAE.MOD_ID, "part/adv_pattern_encoding_terminal_off"),
			new ResourceLocation(AdvancedAE.MOD_ID, "part/adv_pattern_encoding_terminal_on")
	);

	public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, MODELS.get(0), MODEL_STATUS_OFF);
	public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, MODELS.get(1), MODEL_STATUS_ON);
	public static final IPartModel MODELS_HAS_CHANNEL = new PartModel(MODEL_BASE, MODELS.get(1),
			MODEL_STATUS_HAS_CHANNEL);

	private final PatternEncodingLogic logic = new PatternEncodingLogic(this);

	public AdvPatternEncodingTermPart(IPartItem<?> partItem) {
		super(partItem);
	}

	@Override
	public void addAdditionalDrops(List<ItemStack> drops, boolean wrenched) {
		super.addAdditionalDrops(drops, wrenched);
		for (var is : this.logic.getBlankPatternInv()) {
			drops.add(is);
		}
		for (var is : this.logic.getEncodedPatternInv()) {
			drops.add(is);
		}
	}

	@Override
	public void clearContent() {
		super.clearContent();
		this.logic.getBlankPatternInv().clear();
		this.logic.getEncodedPatternInv().clear();
	}

	@Override
	public void readFromNBT(CompoundTag data) {
		super.readFromNBT(data);

		logic.readFromNBT(data);
	}

	@Override
	public void writeToNBT(CompoundTag data) {
		super.writeToNBT(data);
		logic.writeToNBT(data);
	}

	@Override
	public MenuType<?> getMenuType(Player p) {
		return PatternEncodingTermMenu.TYPE;
	}

	@Override
	public IPartModel getStaticModels() {
		return this.selectModel(MODELS_OFF, MODELS_ON, MODELS_HAS_CHANNEL);
	}

	@Override
	public PatternEncodingLogic getLogic() {
		return logic;
	}

	@Override
	public void markForSave() {
		getHost().markForSave();
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return LazyOptional.of(() -> logic.getBlankPatternInv().toItemHandler()).cast();
		}
		return super.getCapability(cap);
	}
}
