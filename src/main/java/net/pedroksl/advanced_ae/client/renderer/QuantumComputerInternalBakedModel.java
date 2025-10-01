package net.pedroksl.advanced_ae.client.renderer;

import java.util.HashMap;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;

public class QuantumComputerInternalBakedModel extends QuantumComputerBaseBakedModel {
    public QuantumComputerInternalBakedModel(
            TextureAtlasSprite face,
            TextureAtlasSprite side,
            TextureAtlasSprite poweredSides,
            TextureAtlasSprite faceAnimation,
            TextureAtlasSprite faceTopAnimation,
            TextureAtlasSprite faceBottomAnimation) {
        super(RenderType.CUTOUT, face, side, poweredSides, block -> block.type != AAECraftingUnitType.STRUCTURE);

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
}
