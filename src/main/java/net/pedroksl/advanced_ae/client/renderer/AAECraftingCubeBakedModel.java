package net.pedroksl.advanced_ae.client.renderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;

import appeng.blockentity.crafting.CraftingCubeModelData;
import appeng.client.render.cablebus.CubeBuilder;
import appeng.util.Platform;

abstract class AAECraftingCubeBakedModel implements IDynamicBakedModel {
    private static final ChunkRenderTypeSet RENDER_TYPES = ChunkRenderTypeSet.of(RenderType.CUTOUT);

    private final TextureAtlasSprite ringCorner;

    private final TextureAtlasSprite ringHor;

    private final TextureAtlasSprite ringVer;

    AAECraftingCubeBakedModel(TextureAtlasSprite ringCorner, TextureAtlasSprite ringHor, TextureAtlasSprite ringVer) {
        this.ringCorner = ringCorner;
        this.ringHor = ringHor;
        this.ringVer = ringVer;
    }

    @Override
    public List<BakedQuad> getQuads(
            @Nullable BlockState state,
            @Nullable Direction side,
            RandomSource rand,
            ModelData extraData,
            RenderType renderType) {
        if (side == null) {
            return Collections.emptyList(); // No generic quads for this model
        }

        EnumSet<Direction> connections = getConnections(extraData);

        List<BakedQuad> quads = new ArrayList<>();
        CubeBuilder builder = new CubeBuilder(quads);

        builder.setDrawFaces(EnumSet.of(side));

        // Add the quads for the ring that frames the entire multi-block structure
        this.addRing(builder, side, connections);

        // Calculate the bounds of the "inner" block that is framed by the border drawn
        // above
        float x2 = connections.contains(Direction.EAST) ? 16 : 14.01f;
        float x1 = connections.contains(Direction.WEST) ? 0 : 1.99f;

        float y2 = connections.contains(Direction.UP) ? 16 : 14.01f;
        float y1 = connections.contains(Direction.DOWN) ? 0 : 1.99f;

        float z2 = connections.contains(Direction.SOUTH) ? 16 : 14.01f;
        float z1 = connections.contains(Direction.NORTH) ? 0 : 1.99f;

        // On the axis of the side that we're currently drawing, extend the dimensions
        // out to the outer face of the block
        switch (side) {
            case DOWN, UP -> {
                y1 = 0;
                y2 = 16;
            }
            case NORTH, SOUTH -> {
                z1 = 0;
                z2 = 16;
            }
            case WEST, EAST -> {
                x1 = 0;
                x2 = 16;
            }
        }

        this.addInnerCube(side, state, extraData, builder, x1, y1, z1, x2, y2, z2);

        return quads;
    }

    private void addRing(CubeBuilder builder, Direction side, EnumSet<Direction> connections) {
        // Fill in the corners
        builder.setTexture(this.ringCorner);
        this.addCornerCap(builder, connections, side, Direction.UP, Direction.EAST, Direction.NORTH);
        this.addCornerCap(builder, connections, side, Direction.UP, Direction.EAST, Direction.SOUTH);
        this.addCornerCap(builder, connections, side, Direction.UP, Direction.WEST, Direction.NORTH);
        this.addCornerCap(builder, connections, side, Direction.UP, Direction.WEST, Direction.SOUTH);
        this.addCornerCap(builder, connections, side, Direction.DOWN, Direction.EAST, Direction.NORTH);
        this.addCornerCap(builder, connections, side, Direction.DOWN, Direction.EAST, Direction.SOUTH);
        this.addCornerCap(builder, connections, side, Direction.DOWN, Direction.WEST, Direction.NORTH);
        this.addCornerCap(builder, connections, side, Direction.DOWN, Direction.WEST, Direction.SOUTH);

        // Fill in the remaining stripes of the face
        for (Direction a : Direction.values()) {
            if (a == side || a == side.getOpposite()) {
                continue;
            }

            // Select the horizontal or vertical ring texture depending on which side we're
            // filling in
            if (side.getAxis() != Direction.Axis.Y
                    && (a == Direction.NORTH || a == Direction.EAST || a == Direction.WEST || a == Direction.SOUTH)) {
                builder.setTexture(this.ringVer);
            } else if (side.getAxis() == Direction.Axis.Y && (a == Direction.EAST || a == Direction.WEST)) {
                builder.setTexture(this.ringVer);
            } else {
                builder.setTexture(this.ringHor);
            }

            // If there's an adjacent crafting cube block on side a, then the core of the
            // block already extends
            // fully to this side. So only bother drawing the stripe, if there's no
            // connection.
            if (!connections.contains(a)) {
                // Note that since we're drawing something that "looks" 2-dimensional,
                // two of the following will always be 0 and 16.
                float x1 = 0, y1 = 0, z1 = 0, x2 = 16, y2 = 16, z2 = 16;

                switch (a) {
                    case DOWN -> {
                        y1 = 0;
                        y2 = 2;
                    }
                    case UP -> {
                        y1 = 14.0f;
                        y2 = 16;
                    }
                    case WEST -> {
                        x1 = 0;
                        x2 = 2;
                    }
                    case EAST -> {
                        x1 = 14;
                        x2 = 16;
                    }
                    case NORTH -> {
                        z1 = 0;
                        z2 = 2;
                    }
                    case SOUTH -> {
                        z1 = 14;
                        z2 = 16;
                    }
                }

                // Constraint the stripe in the two directions perpendicular to a in case there
                // has been a corner
                // drawn in those directions. Since a corner is drawn if the three touching
                // faces dont have adjacent
                // crafting cube blocks, we'd have to check for a, side, and the perpendicular
                // direction. But in this
                // block, we've already checked for side (due to face culling) and a (see
                // above).
                Direction perpendicular = Platform.rotateAround(a, side);
                for (Direction cornerCandidate : EnumSet.of(perpendicular, perpendicular.getOpposite())) {
                    if (!connections.contains(cornerCandidate)) {
                        // There's a cap in this direction
                        switch (cornerCandidate) {
                            case DOWN -> y1 = 2;
                            case UP -> y2 = 14;
                            case NORTH -> z1 = 2;
                            case SOUTH -> z2 = 14;
                            case WEST -> x1 = 2;
                            case EAST -> x2 = 14;
                        }
                    }
                }

                builder.addCube(x1, y1, z1, x2, y2, z2);
            }
        }
    }

    /**
     * Adds a 3x3x3 corner cap to the cube builder if there are no adjacent crafting cubes on that corner.
     */
    private void addCornerCap(
            CubeBuilder builder,
            EnumSet<Direction> connections,
            Direction side,
            Direction down,
            Direction west,
            Direction north) {
        if (connections.contains(down) || connections.contains(west) || connections.contains(north)) {
            return;
        }

        // Only add faces for sides that can actually be seen (the outside of the cube)
        if (side != down && side != west && side != north) {
            return;
        }

        float x1 = west == Direction.WEST ? 0 : 14;
        float y1 = down == Direction.DOWN ? 0 : 14;
        float z1 = north == Direction.NORTH ? 0 : 14;
        float x2 = west == Direction.WEST ? 2 : 16;
        float y2 = down == Direction.DOWN ? 2 : 16;
        float z2 = north == Direction.NORTH ? 2 : 16;
        builder.addCube(x1, y1, z1, x2, y2, z2);
    }

    // Retrieve the cube connection state from the block state
    // If none is present, just assume there are no adjacent crafting cube blocks
    private static EnumSet<Direction> getConnections(ModelData modelData) {
        if (modelData.has(CraftingCubeModelData.CONNECTIONS)) {
            return modelData.get(CraftingCubeModelData.CONNECTIONS);
        }
        return EnumSet.noneOf(Direction.class);
    }

    protected abstract void addInnerCube(
            Direction facing,
            BlockState state,
            ModelData modelData,
            CubeBuilder builder,
            float x1,
            float y1,
            float z1,
            float x2,
            float y2,
            float z2);

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.ringCorner;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return RENDER_TYPES;
    }
}
