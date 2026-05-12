package net.pedroksl.advanced_ae.client.renderer;

import static net.pedroksl.advanced_ae.AdvancedAE.makeId;

import java.util.EnumMap;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitType;
import net.pedroksl.ae2addonlib.client.render.ConnectedTexturesBaseBakedModel;

public class QuantumComputerModel extends ConnectedTexturesBaseBakedModel {

    private final boolean isStructure;

    public QuantumComputerModel(
            Material.Baked face,
            Material.Baked side,
            Material.Baked poweredSides,
            Material.Baked faceAnimation,
            Material.Baked faceTopAnimation,
            Material.Baked faceBottomAnimation) {
        super(RenderTypes.entityCutout(null), face, side, poweredSides);

        isStructure = true;

        setSideEmissive(true);

        EnumMap<Direction, Material.Baked> animationMap = new EnumMap<>(Direction.class);
        animationMap.put(Direction.UP, faceTopAnimation);
        animationMap.put(Direction.DOWN, faceBottomAnimation);
        animationMap.put(Direction.NORTH, faceAnimation);
        animationMap.put(Direction.WEST, faceAnimation);
        animationMap.put(Direction.SOUTH, faceAnimation);
        animationMap.put(Direction.EAST, faceAnimation);
        setFaceAnimation(animationMap, true);
    }

    public QuantumComputerModel(Material.Baked face, Material.Baked sides, Material.Baked poweredSides) {
        // TODO: Fix Render Types for custom model
        super(RenderTypes.entityTranslucent(null), RenderTypes.entityCutout(null), face, sides, poweredSides);

        isStructure = true;

        setSideEmissive(true);
        setRenderOppositeSide(true);
    }

    @Override
    protected boolean shouldConnect(Block block) {
        return isStructure;
    }

    @Override
    protected boolean shouldBeEmissive(BlockState state) {
        return state.getBlock() instanceof AAECraftingUnitBlock && state.getValue(AAECraftingUnitBlock.POWERED);
    }

    public record Unbaked(AAECraftingUnitType type) implements CustomUnbakedBlockStateModel {
        public static final Identifier ID = makeId("quantum_computer_formed");
        public static final MapCodec<QuantumComputerModel.Unbaked> MAP_CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(AAECraftingUnitType.CODEC
                                .fieldOf("unit_type")
                                .forGetter(QuantumComputerModel.Unbaked::type))
                        .apply(instance, QuantumComputerModel.Unbaked::new));

        @Override
        public BlockStateModel bake(ModelBaker baker) {
            var provider = new AAECraftingUnitModelProvider(type);
            return provider.bake(baker.materials());
        }

        @Override
        public void resolveDependencies(Resolver resolver) {}

        @Override
        public MapCodec<QuantumComputerModel.Unbaked> codec() {
            return MAP_CODEC;
        }
    }
}
