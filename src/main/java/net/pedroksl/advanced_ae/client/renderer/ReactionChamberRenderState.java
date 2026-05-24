package net.pedroksl.advanced_ae.client.renderer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;

import appeng.api.orientation.BlockOrientation;

public class ReactionChamberRenderState extends BlockEntityRenderState {
    List<ItemStackRenderState> items = new ArrayList<>();
    TextureAtlasSprite fluidTexture;
    int fluidTint;
    BlockOrientation orientation;

    public void initItems() {
        for (var x = 0; x < ReactionChamberEntity.MAX_INPUT_SLOTS; x++) {
            items.add(new ItemStackRenderState());
        }
    }
}
