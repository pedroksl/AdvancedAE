package net.pedroksl.advanced_ae.client.renderer;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.pedroksl.advanced_ae.common.blocks.AAEAbstractCraftingUnitBlock;

import appeng.client.render.cablebus.CubeBuilder;

public class AnimatedCraftingCubeBakedModel extends AAECraftingCubeBakedModel {

    private final TextureAtlasSprite baseTexture;

    private final TextureAtlasSprite animationTexture;

    public AnimatedCraftingCubeBakedModel(
            TextureAtlasSprite ringCorner,
            TextureAtlasSprite ringHor,
            TextureAtlasSprite ringVer,
            TextureAtlasSprite baseTexture,
            TextureAtlasSprite animationTexture) {
        super(ringCorner, ringHor, ringVer);

        this.baseTexture = baseTexture;
        this.animationTexture = animationTexture;
    }

    @Override
    protected void addInnerCube(
            Direction facing,
            BlockState state,
            ModelData modelData,
            CubeBuilder builder,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2) {
        boolean powered = state.getValue(AAEAbstractCraftingUnitBlock.POWERED);
        var lightLevel = state.getValue(AAEAbstractCraftingUnitBlock.LIGHT_LEVEL);

        builder.setTexture(this.baseTexture);
        builder.addCube(x1, y1, z1, x2, y2, z2);

        if (powered) {
            if (lightLevel > 0) {
                builder.setEmissiveMaterial(true);
            }
            builder.setTexture(this.animationTexture);
            builder.addCube(x1, y1, z1, x2, y2, z2);
            // Reset back to default
            builder.setEmissiveMaterial(false);
        }
    }
}
