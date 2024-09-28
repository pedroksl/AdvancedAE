package net.pedroksl.advanced_ae.common.definitions;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.api.IDirectionalOutputHost;
import net.pedroksl.advanced_ae.common.entities.AdvCraftingBlockEntity;
import net.pedroksl.advanced_ae.common.entities.QuantumCrafterEntity;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;
import net.pedroksl.advanced_ae.common.inventory.AdvPatternEncoderHost;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;
import net.pedroksl.advanced_ae.common.parts.StockExportBusPart;
import net.pedroksl.advanced_ae.gui.*;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;
import net.pedroksl.advanced_ae.gui.advpatternprovider.SmallAdvPatternProviderMenu;
import net.pedroksl.advanced_ae.gui.quantumcomputer.QuantumComputerMenu;

import appeng.menu.AEBaseMenu;
import appeng.menu.implementations.MenuTypeBuilder;

public class AAEMenus {
    public static final DeferredRegister<MenuType<?>> DR = DeferredRegister.create(Registries.MENU, AdvancedAE.MOD_ID);

    public static final MenuType<QuantumComputerMenu> QUANTUM_COMPUTER =
            create("quantum_computer", QuantumComputerMenu::new, AdvCraftingBlockEntity.class);

    public static final MenuType<AdvPatternProviderMenu> ADV_PATTERN_PROVIDER =
            create("adv_pattern_provider", AdvPatternProviderMenu::new, AdvPatternProviderLogicHost.class);
    public static final MenuType<SmallAdvPatternProviderMenu> SMALL_ADV_PATTERN_PROVIDER =
            create("small_adv_pattern_provider", SmallAdvPatternProviderMenu::new, AdvPatternProviderLogicHost.class);
    public static final MenuType<ReactionChamberMenu> REACTION_CHAMBER =
            create("reaction_chamber", ReactionChamberMenu::new, ReactionChamberEntity.class);
    public static final MenuType<AdvPatternEncoderMenu> ADV_PATTERN_ENCODER =
            create("adv_pattern_encoder", AdvPatternEncoderMenu::new, AdvPatternEncoderHost.class);
    public static final MenuType<QuantumCrafterMenu> QUANTUM_CRAFTER =
            create("quantum_crafter", QuantumCrafterMenu::new, QuantumCrafterEntity.class);

    public static final MenuType<StockExportBusMenu> STOCK_EXPORT_BUS =
            create("stock_export_bus", StockExportBusMenu::new, StockExportBusPart.class);

    public static final MenuType<OutputDirectionMenu> OUTPUT_DIRECTION =
            create("output_direction", OutputDirectionMenu::new, IDirectionalOutputHost.class);
    public static final MenuType<QuantumCrafterConfigPatternMenu> CRAFTER_PATTERN_CONFIG =
            create("quantum_crafter_pattern_config", QuantumCrafterConfigPatternMenu::new, QuantumCrafterEntity.class);

    private static <M extends AEBaseMenu, H> MenuType<M> create(
            String id, MenuTypeBuilder.MenuFactory<M, H> factory, Class<H> host) {
        var menu = MenuTypeBuilder.create(factory, host).build(AdvancedAE.makeId(id));
        DR.register(id, () -> menu);
        return menu;
    }
}
