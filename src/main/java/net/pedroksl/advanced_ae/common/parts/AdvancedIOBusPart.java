package net.pedroksl.advanced_ae.common.parts;

import org.jetbrains.annotations.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.helpers.FilteredImportStackTransferContext;

import appeng.api.behaviors.StackImportStrategy;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.util.KeyTypeSelection;
import appeng.core.AppEng;
import appeng.core.definitions.AEItems;
import appeng.items.parts.PartModels;
import appeng.menu.ISubMenu;
import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import appeng.parts.PartModel;
import appeng.parts.automation.StackWorldBehaviors;

@SuppressWarnings("UnstableApiUsage")
public class AdvancedIOBusPart extends StockExportBusPart {

    public static final ResourceLocation MODEL_BASE = AdvancedAE.makeId("part/advanced_io_bus_part");

    @PartModels
    public static final IPartModel MODELS_OFF = new PartModel(MODEL_BASE, AppEng.makeId("part/export_bus_off"));

    @PartModels
    public static final IPartModel MODELS_ON = new PartModel(MODEL_BASE, AppEng.makeId("part/export_bus_on"));

    @PartModels
    public static final IPartModel MODELS_HAS_CHANNEL =
            new PartModel(MODEL_BASE, AppEng.makeId("part/export_bus_has_channel"));

    @Nullable
    private StackImportStrategy importStrategy;

    private final KeyTypeSelection keyTypeSelection;

    public AdvancedIOBusPart(IPartItem<?> partItem) {
        super(partItem);

        this.keyTypeSelection = new KeyTypeSelection(
                () -> {
                    getHost().markForSave();
                    // Reset strategies
                    importStrategy = null;
                    // We can potentially wake up now
                    getMainNode()
                            .ifPresent((grid, node) -> grid.getTickManager().alertDevice(node));
                },
                StackWorldBehaviors.hasImportStrategyTypeFilter());
    }

    public StackImportStrategy getImportStrategy() {
        if (this.importStrategy == null) {
            var self = this.getHost().getBlockEntity();
            var fromPos = self.getBlockPos().relative(this.getSide());
            var fromSide = getSide().getOpposite();
            importStrategy = StackWorldBehaviors.createImportFacade(
                    (ServerLevel) getLevel(), fromPos, fromSide, keyTypeSelection.enabledPredicate());
        }
        return this.importStrategy;
    }

    @Override
    protected boolean doBusWork(IGrid grid) {
        var exportWork = super.doBusWork(grid);

        IStorageService storageService = grid.getStorageService();
        ICraftingService cg = grid.getCraftingService();

        var strategy = getImportStrategy();

        var context = new FilteredImportStackTransferContext(
                grid.getStorageService(), grid.getEnergyService(), this.source, getOperationsPerTick(), getFilter());

        context.setInverted(this.isUpgradedWith(AEItems.INVERTER_CARD));
        strategy.transfer(context);

        var importWork = context.hasDoneWork();

        return exportWork || importWork;
    }

    @Override
    protected MenuType<?> getMenuType() {
        return AAEMenus.ADVANCED_IO_BUS.get();
    }

    @Override
    protected int getOperationsPerTick() {
        return super.getOperationsPerTick() * 8;
    }

    @Override
    public void returnToMainMenu(Player player, ISubMenu subMenu) {
        MenuOpener.open(AAEMenus.ADVANCED_IO_BUS.get(), player, MenuLocators.forPart(this));
    }

    @Override
    public IPartModel getStaticModels() {
        if (this.isActive() && this.isPowered()) {
            return MODELS_HAS_CHANNEL;
        } else if (this.isPowered()) {
            return MODELS_ON;
        } else {
            return MODELS_OFF;
        }
    }
}
