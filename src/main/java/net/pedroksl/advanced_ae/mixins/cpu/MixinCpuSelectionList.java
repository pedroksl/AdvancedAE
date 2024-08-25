package net.pedroksl.advanced_ae.mixins.cpu;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import appeng.client.gui.widgets.CPUSelectionList;
import appeng.menu.me.crafting.CraftingStatusMenu;

@Mixin(value = CPUSelectionList.class, remap = false)
public class MixinCpuSelectionList {

    @ModifyArg(
            method = "drawBackgroundLayer",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lappeng/client/gui/widgets/InfoBar;add(Lappeng/client/gui/Icon;FII)V",
                            ordinal = 1),
            index = 2)
    private int onAddIcon(int xPos) {
        return xPos + 4;
    }

    @ModifyArg(
            method = "drawBackgroundLayer",
            at =
                    @At(
                            value = "INVOKE",
                            target = "Lappeng/client/gui/widgets/InfoBar;add(Ljava/lang/String;IFII)V",
                            ordinal = 1),
            index = 3)
    private int onAddString(int xPos) {
        return xPos + 4;
    }

    /**
     * @author pedroksl
     * @reason Better formatting for bigger storage cpus
     */
    @Overwrite
    private String formatStorage(CraftingStatusMenu.CraftingCpuListEntry cpu) {
        if (cpu.storage() > (1024L * 1024 * 10)) { // Storage if bigger than 10M
            return (cpu.storage() / 1024L / 1024) + "M";
        }
        return (cpu.storage() / 1024) + "k";
    }
}
