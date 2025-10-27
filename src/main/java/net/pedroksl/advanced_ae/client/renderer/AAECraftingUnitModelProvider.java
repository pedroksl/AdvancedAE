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

    protected static final Material STRUCTURE_FORMED_FACE = texture("quantum_structure_formed_face");
    protected static final Material STRUCTURE_FORMED_SIDES = texture("quantum_structure_formed_sides");
    protected static final Material STRUCTURE_ANIMATION_SIDES = texture("quantum_structure_powered_sides");

    protected static final Material INTERNAL_FORMED_FACE = texture("quantum_internal_formed_face");
    protected static final Material INTERNAL_FORMED_SIDES = texture("quantum_internal_formed_sides");
    protected static final Material INTERNAL_ANIMATION_SIDES = texture("quantum_internal_powered_sides");

    protected static final Material INTERNAL_ANIMATION_FACE = texture("quantum_internal_powered_animation");
    protected static final Material INTERNAL_ANIMATION_FACE_TB = texture("quantum_internal_powered_animation_tb");

    public AAECraftingUnitModelProvider(AAECraftingUnitType type) {
        super(type);
    }

    @Override
    public List<Material> getMaterials() {
        return Collections.unmodifiableList(MATERIALS);
    }

    @Override
    public BakedModel getBakedModel(Function<Material, TextureAtlasSprite> spriteGetter) {
        if (type == AAECraftingUnitType.STRUCTURE) {
            return new QuantumComputerStructureBakedModel(
                    spriteGetter.apply(STRUCTURE_FORMED_FACE),
                    spriteGetter.apply(STRUCTURE_FORMED_SIDES),
                    spriteGetter.apply(STRUCTURE_ANIMATION_SIDES));
        } else {
            return new QuantumComputerInternalBakedModel(
                    spriteGetter.apply(INTERNAL_FORMED_FACE),
                    spriteGetter.apply(INTERNAL_FORMED_SIDES),
                    spriteGetter.apply(INTERNAL_ANIMATION_SIDES),
                    spriteGetter.apply(INTERNAL_ANIMATION_FACE),
                    spriteGetter.apply(INTERNAL_ANIMATION_FACE_TB),
                    spriteGetter.apply(INTERNAL_ANIMATION_FACE_TB));
        }
    }

    private static Material texture(String name) {
        var material = new Material(InventoryMenu.BLOCK_ATLAS, AdvancedAE.makeId("block/crafting/" + name));
        MATERIALS.add(material);
        return material;
    }
}
