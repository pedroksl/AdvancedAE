package net.pedroksl.advanced_ae.client.renderer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;

import appeng.client.render.crafting.AbstractCraftingUnitModelProvider;

public class AAECraftingUnitModelProvider extends AbstractCraftingUnitModelProvider<AAECraftingUnitType>
        implements ModelDebugName {
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
    public BlockStateModel bake(MaterialBaker materialBaker) {
        if (type == AAECraftingUnitType.QUANTUM_STRUCTURE) {
            return new QuantumComputerModel(
                    materialBaker.get(STRUCTURE_FORMED_FACE, this),
                    materialBaker.get(STRUCTURE_FORMED_SIDES, this),
                    materialBaker.get(STRUCTURE_ANIMATION_SIDES, this));
        } else {
            return new QuantumComputerModel(
                    materialBaker.get(INTERNAL_FORMED_FACE, this),
                    materialBaker.get(INTERNAL_FORMED_SIDES, this),
                    materialBaker.get(INTERNAL_ANIMATION_SIDES, this),
                    materialBaker.get(INTERNAL_ANIMATION_FACE, this),
                    materialBaker.get(INTERNAL_ANIMATION_FACE_TB, this),
                    materialBaker.get(INTERNAL_ANIMATION_FACE_TB, this));
        }
    }

    private static Material texture(String name) {
        return new Material(AdvancedAE.makeId("block/crafting/" + name));
    }

    @Override
    public String debugName() {
        return getClass().toString();
    }
}
