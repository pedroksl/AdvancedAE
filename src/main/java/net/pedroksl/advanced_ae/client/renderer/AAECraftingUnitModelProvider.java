package net.pedroksl.advanced_ae.client.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.inventory.InventoryMenu;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;

import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;

public class AAECraftingUnitModelProvider extends AbstractCraftingUnitModelProvider<AAECraftingUnitType> {
    private static final List<Material> MATERIALS = new ArrayList<>();

    protected static final Material RING_CORNER = texture("ring_corner");
    protected static final Material RING_SIDE_HOR = texture("ring_side_hor");
    protected static final Material RING_SIDE_VER = texture("ring_side_ver");
    protected static final Material STRUCTURE_BASE = texture("quantum_structure_formed");
    protected static final Material STRUCTURE_ANIMATION = texture("quantum_structure_powered");

    public AAECraftingUnitModelProvider(AAECraftingUnitType type) {
        super(type);
    }

    @Override
    public List<Material> getMaterials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    @Override
    public BakedModel getBakedModel(Function<Material, TextureAtlasSprite> spriteGetter) {
        TextureAtlasSprite ringCorner = spriteGetter.apply(RING_CORNER);
        TextureAtlasSprite ringSideHor = spriteGetter.apply(RING_SIDE_HOR);
        TextureAtlasSprite ringSideVer = spriteGetter.apply(RING_SIDE_VER);

        return new AnimatedCraftingCubeBakedModel(
                ringCorner,
                ringSideHor,
                ringSideVer,
                spriteGetter.apply(STRUCTURE_BASE),
                spriteGetter.apply(STRUCTURE_ANIMATION));
    }

    private static Material texture(String name) {
        var material = new Material(InventoryMenu.BLOCK_ATLAS, AdvancedAE.makeId("block/crafting/" + name));
        MATERIALS.add(material);
        return material;
    }
}
