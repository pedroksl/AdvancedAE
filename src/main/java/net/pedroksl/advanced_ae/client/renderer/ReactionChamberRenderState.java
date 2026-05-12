package net.pedroksl.advanced_ae.client.renderer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import appeng.api.orientation.BlockOrientation;

public class ReactionChamberRenderState extends BlockEntityRenderState {
    List<ItemStackRenderState> items = new ArrayList<>();
    TextureAtlasSprite fluidTexture;
    int fluidTint;
    BlockOrientation orientation;

    public void clearState() {
        for (var item : items) {
            item.clear();
        }

        fluidTexture = null;
        fluidTint = -1;
        orientation = null;
    }
}
