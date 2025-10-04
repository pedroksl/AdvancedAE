package net.pedroksl.advanced_ae.client.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;

public class QuantumComputerStructureBakedModel extends QuantumComputerBaseBakedModel {
    public QuantumComputerStructureBakedModel(
            TextureAtlasSprite face, TextureAtlasSprite sides, TextureAtlasSprite poweredSides) {
        super(
                RenderType.TRANSLUCENT,
                RenderType.CUTOUT,
                face,
                sides,
                poweredSides,
                block -> block.type == AAECraftingUnitType.STRUCTURE);

        setSideEmissive(true);
        setRenderOppositeSide(true);
    }
}
