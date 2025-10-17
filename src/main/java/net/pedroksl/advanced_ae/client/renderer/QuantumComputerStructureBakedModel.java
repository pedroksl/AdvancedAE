package net.pedroksl.advanced_ae.client.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.ae2addonlib.client.render.ConnectedTexturesBaseBakedModel;

public class QuantumComputerStructureBakedModel extends ConnectedTexturesBaseBakedModel {
    public QuantumComputerStructureBakedModel(
            TextureAtlasSprite face, TextureAtlasSprite sides, TextureAtlasSprite poweredSides) {
        super(RenderType.TRANSLUCENT, RenderType.CUTOUT, face, sides, poweredSides);

        setSideEmissive(true);
        setRenderOppositeSide(true);
    }

    @Override
    protected boolean shouldConnect(Block block) {
        return block instanceof AAECraftingUnitBlock unit && unit.type == AAECraftingUnitType.STRUCTURE;
    }

    @Override
    protected boolean shouldBeEmissive(BlockState state) {
        return state.getValue(AAECraftingUnitBlock.POWERED);
    }
}
