package net.pedroksl.advanced_ae.common;

import appeng.api.parts.PartModels;
import appeng.block.AEBaseBlockItem;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.AEBaseBlockEntity;
import appeng.blockentity.ClientTickingBlockEntity;
import appeng.blockentity.ServerTickingBlockEntity;
import appeng.core.AppEng;
import appeng.items.AEBaseItem;
import net.pedroksl.advanced_ae.xmod.appflux.AFCommonLoad;
import com.glodblock.github.glodium.registry.RegistryHandler;
import com.glodblock.github.glodium.util.GlodUtil;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.parts.AdvPatternProviderPart;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderContainer;
import net.pedroksl.advanced_ae.gui.patternencoder.AdvPatternEncoderContainer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public class AAERegistryHandler extends RegistryHandler {

	public static final AAERegistryHandler INSTANCE = new AAERegistryHandler();

	public AAERegistryHandler() {
		super(AdvancedAE.MOD_ID);
	}

	public <T extends AEBaseBlockEntity> void block(String name, AEBaseEntityBlock<T> block, Class<T> clazz,
	                                                BlockEntityType.BlockEntitySupplier<T> supplier) {
		bindTileEntity(clazz, block, supplier);
		block(name, block, b -> new AEBaseBlockItem(b, new Item.Properties()));
		for (var pair : tiles) {
			if (pair.getValue() == block.getBlockEntityType()) {
				return;
			}
		}
		tile(name, block.getBlockEntityType());
	}

	@Override
	public void register(RegisterEvent event) {
		super.register(event);
		this.onRegisterContainer();
		this.onRegisterModels(); // Parts
	}

	public Collection<Block> getBlocks() {
		return this.blocks.stream().map(Pair::getRight).toList();
	}

	private void onRegisterContainer() {
		ForgeRegistries.MENU_TYPES.register(AppEng.makeId("adv_pattern_provider"), AdvPatternProviderContainer.TYPE);
		ForgeRegistries.MENU_TYPES.register(AppEng.makeId("adv_pattern_encoder"), AdvPatternEncoderContainer.TYPE);
	}

	private <T extends AEBaseBlockEntity> void bindTileEntity(Class<T> clazz, AEBaseEntityBlock<T> block, BlockEntityType.BlockEntitySupplier<T> supplier) {
		BlockEntityTicker<T> serverTicker = null;
		if (ServerTickingBlockEntity.class.isAssignableFrom(clazz)) {
			serverTicker = (level, pos, state, entity) -> ((ServerTickingBlockEntity) entity).serverTick();
		}
		BlockEntityTicker<T> clientTicker = null;
		if (ClientTickingBlockEntity.class.isAssignableFrom(clazz)) {
			clientTicker = (level, pos, state, entity) -> ((ClientTickingBlockEntity) entity).clientTick();
		}
		block.setBlockEntity(clazz, GlodUtil.getTileType(clazz, supplier, block), clientTicker, serverTicker);
	}

	public void onInit() {
		for (Pair<String, Block> entry : blocks) {
			Block block = ForgeRegistries.BLOCKS.getValue(AdvancedAE.id(entry.getKey()));
			if (block instanceof AEBaseEntityBlock<?>) {
				AEBaseBlockEntity.registerBlockEntityItem(
						((AEBaseEntityBlock<?>) block).getBlockEntityType(),
						block.asItem()
				);
			}
		}
		this.registerAEUpgrade();
		this.registerPackagedItems();
		if (ModList.get().isLoaded("appflux")) {
			AFCommonLoad.init();
		}
	}

	private void registerAEUpgrade() {
	}

	private void onRegisterModels() {
		PartModels.registerModels(AdvPatternProviderPart.MODELS);
	}

	private void registerPackagedItems() {

	}

	public void registerTab(Registry<CreativeModeTab> registry) {
		var tab = CreativeModeTab.builder()
				.icon(() -> new ItemStack(AAEItemAndBlock.ADV_PATTERN_PROVIDER))
				.title(Component.translatable("itemGroup.app"))
				.displayItems((__, o) -> {
					for (Pair<String, Item> entry : items) {
						if (entry.getRight() instanceof AEBaseItem aeItem) {
							aeItem.addToMainCreativeTab(o);
						} else {
							o.accept(entry.getRight());
						}
					}
					for (Pair<String, Block> entry : blocks) {
						o.accept(entry.getRight());
					}
				})
				.build();
		Registry.register(registry, AdvancedAE.id("tab_main"), tab);
	}
}
