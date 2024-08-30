package net.pedroksl.advanced_ae.gui.reactionchamber;

import net.minecraft.world.entity.player.Inventory;
import net.pedroksl.advanced_ae.common.definitions.AAEMenus;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;

import appeng.menu.implementations.UpgradeableMenu;
import appeng.menu.interfaces.IProgressProvider;

public class ReactionChamberMenu extends UpgradeableMenu<ReactionChamberEntity> implements IProgressProvider {

    public ReactionChamberMenu(int id, Inventory ip, ReactionChamberEntity host) {
        super(AAEMenus.REACTION_CHAMBER, id, ip, host);
    }

    @Override
    public int getCurrentProgress() {
        return 0;
    }

    @Override
    public int getMaxProgress() {
        return 0;
    }
}
