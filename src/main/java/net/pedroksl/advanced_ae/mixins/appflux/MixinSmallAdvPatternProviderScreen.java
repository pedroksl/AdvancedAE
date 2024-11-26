package net.pedroksl.advanced_ae.mixins.appflux;

import java.util.ArrayList;
import java.util.List;

import com.glodblock.github.appflux.util.helpers.IUpgradableMenu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.client.gui.SmallAdvPatternProviderScreen;
import net.pedroksl.advanced_ae.gui.advpatternprovider.AdvPatternProviderMenu;
import net.pedroksl.advanced_ae.gui.advpatternprovider.SmallAdvPatternProviderMenu;

import appeng.api.upgrades.Upgrades;
import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.ToolboxPanel;
import appeng.client.gui.widgets.UpgradesPanel;
import appeng.core.localization.GuiText;
import appeng.menu.SlotSemantics;

@Mixin(SmallAdvPatternProviderScreen.class)
public abstract class MixinSmallAdvPatternProviderScreen<C extends AdvPatternProviderMenu> extends AEBaseScreen<C> {

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void initUpgrade(
            SmallAdvPatternProviderMenu menu,
            Inventory playerInventory,
            Component title,
            ScreenStyle style,
            CallbackInfo ci) {
        this.widgets.add(
                "upgrades", new UpgradesPanel(menu.getSlots(SlotSemantics.UPGRADE), this::af_$getCompatibleUpgrades));
        if (((IUpgradableMenu) menu).getToolbox().isPresent()) {
            this.widgets.add(
                    "toolbox",
                    new ToolboxPanel(
                            style, ((IUpgradableMenu) menu).getToolbox().getName()));
        }
    }

    @Unique
    private List<Component> af_$getCompatibleUpgrades() {
        var list = new ArrayList<Component>();
        list.add(GuiText.CompatibleUpgrades.text());
        list.addAll(Upgrades.getTooltipLinesForMachine(
                ((IUpgradableMenu) menu).getUpgrades().getUpgradableItem()));
        return list;
    }

    public MixinSmallAdvPatternProviderScreen(C menu, Inventory playerInventory, Component title, ScreenStyle style) {
        super(menu, playerInventory, title, style);
    }
}
