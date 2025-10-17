package net.pedroksl.advanced_ae.client.renderer;

import java.util.HashMap;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.ae2addonlib.client.render.ConnectedTexturesBaseBakedModel;

public class QuantumComputerInternalBakedModel extends ConnectedTexturesBaseBakedModel {
    public QuantumComputerInternalBakedModel(
            TextureAtlasSprite face,
            TextureAtlasSprite side,
            TextureAtlasSprite poweredSides,
            TextureAtlasSprite faceAnimation,
            TextureAtlasSprite faceTopAnimation,
            TextureAtlasSprite faceBottomAnimation) {
        super(RenderType.CUTOUT, face, side, poweredSides);

        setSideEmissive(true);

        HashMap<Direction, TextureAtlasSprite> animationMap = new HashMap<>();
        animationMap.put(Direction.UP, faceTopAnimation);
        animationMap.put(Direction.DOWN, faceBottomAnimation);
        animationMap.put(Direction.NORTH, faceAnimation);
        animationMap.put(Direction.WEST, faceAnimation);
        animationMap.put(Direction.SOUTH, faceAnimation);
        animationMap.put(Direction.EAST, faceAnimation);
        setFaceAnimation(animationMap, true);
    }

    @Override
    protected boolean shouldConnect(Block block) {
        return block instanceof AAECraftingUnitBlock unit && unit.type != AAECraftingUnitType.STRUCTURE;
    }

    @Override
    protected boolean shouldBeEmissive(BlockState state) {
        return state.getValue(AAECraftingUnitBlock.POWERED);
    }
}
