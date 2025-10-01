package net.pedroksl.advanced_ae.client.renderer;

import java.util.*;
import java.util.function.Function;
import javax.annotation.ParametersAreNonnullByDefault;

import net.neoforged.neoforge.client.model.QuadTransformers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import net.pedroksl.advanced_ae.common.blocks.AAEAbstractCraftingUnitBlock;
import net.pedroksl.advanced_ae.common.blocks.AAECraftingUnitBlock;

/**
 * Base Baked Model class heavily inspired by <a href="https://github.com/GlodBlock/ExtendedAE/blob/1.21.1-neoforge/src/main/java/com/glodblock/github/extendedae/client/model/AssemblerGlassBakedModel.java">
 * Extended AE's Assembler Matrix Glass
 * </a>
 */
abstract class QuantumComputerBaseBakedModel implements IDynamicBakedModel {
    private final ChunkRenderTypeSet RENDER_TYPES;

    private static final Object2ReferenceMap<FaceCorner, List<Vector3f>> V_MAP = createVertexMap();
    private static final EnumMap<Direction, List<Vector3f>> F_MAP = createFaceMap();
    public static final ModelProperty<Connect> CONNECT_STATE = new ModelProperty<>();
    private static final int LU = 0;
    private static final int RU = 1;
    private static final int LD = 2;
    private static final int RD = 4;

    private Function<AAECraftingUnitBlock, Boolean> connectCondition;

    private final TextureAtlasSprite face;
    private final TextureAtlasSprite sides;
    private final TextureAtlasSprite poweredSides;
    private HashMap<Direction, TextureAtlasSprite> faceAnimations;

    private boolean renderOppositeSide = false;

    private boolean isFaceEmissive = false;
    private boolean isSideEmissive = false;
    private boolean isFaceAnimationEmissive = false;

    QuantumComputerBaseBakedModel(
            RenderType renderType,
            TextureAtlasSprite face,
            TextureAtlasSprite sides,
            TextureAtlasSprite poweredSides,
            Function<AAECraftingUnitBlock, Boolean> connectCondition) {
        this.RENDER_TYPES = ChunkRenderTypeSet.of(renderType);
        this.face = face;
        this.sides = sides;
        this.poweredSides = poweredSides;
        this.connectCondition = connectCondition;
    }

    public void setFaceEmissive(boolean faceEmissive) {
        this.isFaceEmissive = faceEmissive;
    }

    public void setSideEmissive(boolean sideEmissive) {
        this.isSideEmissive = sideEmissive;
    }

    public void setFaceAnimation(HashMap<Direction, TextureAtlasSprite> faceAnimations, boolean emissive) {
        this.faceAnimations = faceAnimations;
        this.isFaceAnimationEmissive = emissive;
    }

    public void setRenderOppositeSide(boolean renderOppositeSide) {
        this.renderOppositeSide = renderOppositeSide;
    }

    @Override
    @NotNull
    public ModelData getModelData(
            @NotNull BlockAndTintGetter world,
            @NotNull BlockPos pos,
            @NotNull BlockState state,
            @NotNull ModelData modelData) {
        var connect = new Connect();
        connect.init(pos);
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    var offset = pos.offset(x, y, z);
                    if (world.getBlockState(offset)
                                            .getAppearance(world, offset, Direction.NORTH, state, pos)
                                            .getBlock()
                                    instanceof AAECraftingUnitBlock block
                            && connectCondition.apply(block)) {
                        connect.set(x, y, z);
                    }
                }
            }
        }
        return modelData.derive().with(CONNECT_STATE, connect).build();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(
            @Nullable BlockState blockState,
            @Nullable Direction side,
            @NotNull RandomSource randomSource,
            @NotNull ModelData modelData,
            @Nullable RenderType renderType) {
        if (side == null) {
            return Collections.emptyList();
        }
        var connect = modelData.get(CONNECT_STATE);
        if (connect == null) {
            return Collections.emptyList();
        }

        var powered = blockState.getValue(AAEAbstractCraftingUnitBlock.POWERED);
        List<BakedQuad> quads = new ArrayList<>();
        this.addQuad(quads, side, connect.getFace(side), powered);
        if (this.sides != null) {
            addSides(quads, connect, side, powered);

            if (this.renderOppositeSide) {
                addSides(quads, connect, side.getOpposite(), powered, true);
            }
        }
        return quads;
    }

    private void addSides(List<BakedQuad> quads, Connect connect, Direction side, boolean powered) {
        addSides(quads, connect, side, powered, false);
    }

    private void addSides(List<BakedQuad> quads, Connect connect, Direction side, boolean powered, boolean renderOpposite) {
        this.addQuad(quads, side, connect.getIndex(side, LU), LU, powered, renderOpposite);
        this.addQuad(quads, side, connect.getIndex(side, RU), RU, powered, renderOpposite);
        this.addQuad(quads, side, connect.getIndex(side, LD), LD, powered, renderOpposite);
        this.addQuad(quads, side, connect.getIndex(side, RD), RD, powered, renderOpposite);
    }

    private List<Vector3f> calculateCorners(Direction face, int corner) {
        return V_MAP.get(new FaceCorner(face, corner));
    }

    private void addQuad(List<BakedQuad> quads, Direction side, int index, boolean powered) {
        if (index < 0) {
            return;
        }

        var cons = F_MAP.get(side);
        var normal = side.getNormal();
        // Render the face a fraction of a pixel inwards to avoid z-fighting
        var normalF = new Vector3f(getNormalStep(normal.getX()), getNormalStep(normal.getY()), getNormalStep(normal.getZ()));
        var c1 = new Vector3f(cons.get(0)).sub(normalF);
        var c2 = new Vector3f(cons.get(1)).sub(normalF);
        var c3 = new Vector3f(cons.get(2)).sub(normalF);
        var c4 = new Vector3f(cons.get(3)).sub(normalF);

        var builder = new QuadBakingVertexConsumer();
        builder.setSprite(this.face);
        builder.setDirection(side);
        builder.setShade(true);
        this.putVertex(builder, this.face, normal, c1.x(), c1.y(), c1.z(), 0, 0);
        this.putVertex(builder, this.face, normal, c2.x(), c2.y(), c2.z(), 0, 1);
        this.putVertex(builder, this.face, normal, c3.x(), c3.y(), c3.z(), 1, 1);
        this.putVertex(builder, this.face, normal, c4.x(), c4.y(), c4.z(), 1, 0);
        var quad = builder.bakeQuad();
        if (this.isFaceEmissive && powered) {
            QuadTransformers.settingMaxEmissivity()
                    .processInPlace(quad);
        }
        quads.add(quad);

        if (powered && this.faceAnimations != null && this.faceAnimations.get(side) != null) {
            var texture = this.faceAnimations.get(side);
            builder.setSprite(texture);
            builder.setDirection(side);
            builder.setShade(true);
            this.putVertex(builder, texture, normal, c1.x(), c1.y(), c1.z(), 0, 0);
            this.putVertex(builder, texture, normal, c2.x(), c2.y(), c2.z(), 0, 1);
            this.putVertex(builder, texture, normal, c3.x(), c3.y(), c3.z(), 1, 1);
            this.putVertex(builder, texture, normal, c4.x(), c4.y(), c4.z(), 1, 0);

            var aniQuad = builder.bakeQuad();
            if (this.isFaceAnimationEmissive) {
                QuadTransformers.settingMaxEmissivity()
                        .processInPlace(aniQuad);
            }
            quads.add(aniQuad);
        }
    }

    private void addQuad(List<BakedQuad> quads, Direction side, int index, int corner, boolean powered, boolean renderOpposite) {
        if (index < 0) {
            return;
        }
        var builder = new QuadBakingVertexConsumer();

        var cons = this.calculateCorners(side, corner);
        var texture = powered ? this.poweredSides : this.sides;
        builder.setSprite(texture);
        builder.setDirection(side);
        builder.setShade(true);
        var normal = side.getNormal();
        var c1 = renderOpposite ? cons.get(3) : cons.get(0);
        var c2 = renderOpposite ? cons.get(2) : cons.get(1);
        var c3 = renderOpposite ? cons.get(1) : cons.get(2);
        var c4 = renderOpposite ? cons.get(0) : cons.get(3);
        if (renderOpposite) {
            // Render the face a fraction of a pixel inwards to avoid z-fighting
            var normalF = new Vector3f(getNormalStep(normal.getX()), getNormalStep(normal.getY()), getNormalStep(normal.getZ()));
            c1 = new Vector3f(c1).sub(normalF);
            c2 = new Vector3f(c2).sub(normalF);
            c3 = new Vector3f(c3).sub(normalF);
            c4 = new Vector3f(c4).sub(normalF);
        }
        float u0 = renderOpposite ? this.getU1(index) : this.getU0(index);
        float u1 = renderOpposite ? this.getU0(index) : this.getU1(index);
        float v0 = this.getV0(index);
        float v1 = this.getV1(index);
        switch (corner) {
            case LU -> {
                this.putVertex(builder, texture, normal, c1.x(), c1.y(), c1.z(), u0, v0);
                this.putVertex(builder, texture, normal, c2.x(), c2.y(), c2.z(), u0, v1);
                this.putVertex(builder, texture, normal, c3.x(), c3.y(), c3.z(), u1, v1);
                this.putVertex(builder, texture, normal, c4.x(), c4.y(), c4.z(), u1, v0);
            }
            case RU -> {
                this.putVertex(builder, texture, normal, c1.x(), c1.y(), c1.z(), u1, v0);
                this.putVertex(builder, texture, normal, c2.x(), c2.y(), c2.z(), u1, v1);
                this.putVertex(builder, texture, normal, c3.x(), c3.y(), c3.z(), u0, v1);
                this.putVertex(builder, texture, normal, c4.x(), c4.y(), c4.z(), u0, v0);
            }
            case LD -> {
                this.putVertex(builder, texture, normal, c1.x(), c1.y(), c1.z(), u0, v1);
                this.putVertex(builder, texture, normal, c2.x(), c2.y(), c2.z(), u0, v0);
                this.putVertex(builder, texture, normal, c3.x(), c3.y(), c3.z(), u1, v0);
                this.putVertex(builder, texture, normal, c4.x(), c4.y(), c4.z(), u1, v1);
            }
            case RD -> {
                this.putVertex(builder, texture, normal, c1.x(), c1.y(), c1.z(), u1, v1);
                this.putVertex(builder, texture, normal, c2.x(), c2.y(), c2.z(), u1, v0);
                this.putVertex(builder, texture, normal, c3.x(), c3.y(), c3.z(), u0, v0);
                this.putVertex(builder, texture, normal, c4.x(), c4.y(), c4.z(), u0, v1);
            }
        }
        var quad = builder.bakeQuad();
        if (this.isSideEmissive && powered) {
            QuadTransformers.settingMaxEmissivity()
                    .processInPlace(quad);
        }
        quads.add(quad);
    }

    private static EnumMap<Direction, List<Vector3f>> createFaceMap() {
        // spotless:off
        EnumMap<Direction, List<Vector3f>> map = new EnumMap<>(Direction.class);
        map.put(Direction.EAST, List.of(new Vector3f(1, 1, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0, 0), new Vector3f(1, 1, 0)));
        map.put(Direction.WEST, List.of(new Vector3f(0, 1, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0)).reversed());
        map.put(Direction.UP, List.of(new Vector3f(1, 1, 1), new Vector3f(1, 1, 0), new Vector3f(0, 1, 0), new Vector3f(0, 1, 1)));
        map.put(Direction.DOWN, List.of(new Vector3f(1, 0, 1), new Vector3f(1, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 1)).reversed());
        map.put(Direction.SOUTH, List.of(new Vector3f(0, 1, 1), new Vector3f(0, 0, 1), new Vector3f(1, 0, 1), new Vector3f(1, 1, 1)));
        map.put(Direction.NORTH, List.of(new Vector3f(0, 1, 0), new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), new Vector3f(1, 1, 0)).reversed());
        //spotless:on
        return map;
    }

    private static Object2ReferenceMap<FaceCorner, List<Vector3f>> createVertexMap() {
        // spotless:off
        Object2ReferenceMap<FaceCorner, List<Vector3f>> map = new Object2ReferenceOpenHashMap<>();
        map.put(new FaceCorner(Direction.EAST, LU), List.of(new Vector3f(1, 1, 1), new Vector3f(1, 0.5f, 1), new Vector3f(1, 0.5f, 0.5f), new Vector3f(1, 1, 0.5f)));
        map.put(new FaceCorner(Direction.EAST, RU), List.of(new Vector3f(1, 1, 0.5f), new Vector3f(1, 0.5f, 0.5f), new Vector3f(1, 0.5f, 0), new Vector3f(1, 1, 0)));
        map.put(new FaceCorner(Direction.EAST, LD), List.of(new Vector3f(1, 0.5f, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0, 0.5f), new Vector3f(1, 0.5f, 0.5f)));
        map.put(new FaceCorner(Direction.EAST, RD), List.of(new Vector3f(1, 0.5f, 0.5f), new Vector3f(1, 0, 0.5f), new Vector3f(1, 0, 0), new Vector3f(1, 0.5f, 0)));
        map.put(new FaceCorner(Direction.WEST, LU), List.of(new Vector3f(0, 1, 0), new Vector3f(0, 0.5f, 0), new Vector3f(0, 0.5f, 0.5f), new Vector3f(0, 1, 0.5f)));
        map.put(new FaceCorner(Direction.WEST, RU), List.of(new Vector3f(0, 1, 0.5f), new Vector3f(0, 0.5f, 0.5f), new Vector3f(0, 0.5f, 1), new Vector3f(0, 1, 1)));
        map.put(new FaceCorner(Direction.WEST, LD), List.of(new Vector3f(0, 0.5f, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0.5f), new Vector3f(0, 0.5f, 0.5f)));
        map.put(new FaceCorner(Direction.WEST, RD), List.of(new Vector3f(0, 0.5f, 0.5f), new Vector3f(0, 0, 0.5f), new Vector3f(0, 0, 1), new Vector3f(0, 0.5f, 1)));
        map.put(new FaceCorner(Direction.SOUTH, LU), List.of(new Vector3f(0, 1, 1), new Vector3f(0, 0.5f, 1), new Vector3f(0.5f, 0.5f, 1), new Vector3f(0.5f, 1, 1)));
        map.put(new FaceCorner(Direction.SOUTH, RU), List.of(new Vector3f(0.5f, 1, 1), new Vector3f(0.5f, 0.5f, 1), new Vector3f(1, 0.5f, 1), new Vector3f(1, 1, 1)));
        map.put(new FaceCorner(Direction.SOUTH, LD), List.of(new Vector3f(0, 0.5f, 1), new Vector3f(0, 0, 1), new Vector3f(0.5f, 0, 1), new Vector3f(0.5f, 0.5f, 1)));
        map.put(new FaceCorner(Direction.SOUTH, RD), List.of(new Vector3f(0.5f, 0.5f, 1), new Vector3f(0.5f, 0, 1), new Vector3f(1, 0, 1), new Vector3f(1, 0.5f, 1)));
        map.put(new FaceCorner(Direction.NORTH, LU), List.of(new Vector3f(1, 1, 0), new Vector3f(1, 0.5f, 0), new Vector3f(0.5f, 0.5f, 0), new Vector3f(0.5f, 1, 0)));
        map.put(new FaceCorner(Direction.NORTH, RU), List.of(new Vector3f(0.5f, 1, 0), new Vector3f(0.5f, 0.5f, 0), new Vector3f(0, 0.5f, 0), new Vector3f(0, 1, 0)));
        map.put(new FaceCorner(Direction.NORTH, LD), List.of(new Vector3f(1, 0.5f, 0), new Vector3f(1, 0, 0), new Vector3f(0.5f, 0, 0), new Vector3f(0.5f, 0.5f, 0)));
        map.put(new FaceCorner(Direction.NORTH, RD), List.of(new Vector3f(0.5f, 0.5f, 0), new Vector3f(0.5f, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0.5f, 0)));
        map.put(new FaceCorner(Direction.UP, LU), List.of(new Vector3f(0, 1, 1), new Vector3f(0.5f, 1, 1), new Vector3f(0.5f, 1, 0.5f), new Vector3f(0, 1, 0.5f)));
        map.put(new FaceCorner(Direction.UP, RU), List.of(new Vector3f(0, 1, 0.5f), new Vector3f(0.5f, 1, 0.5f), new Vector3f(0.5f, 1, 0), new Vector3f(0, 1, 0)));
        map.put(new FaceCorner(Direction.UP, LD), List.of(new Vector3f(0.5f, 1, 1), new Vector3f(1, 1, 1), new Vector3f(1, 1, 0.5f), new Vector3f(0.5f, 1, 0.5f)));
        map.put(new FaceCorner(Direction.UP, RD), List.of(new Vector3f(0.5f, 1, 0.5f), new Vector3f(1, 1, 0.5f), new Vector3f(1, 1, 0), new Vector3f(0.5f, 1, 0)));
        map.put(new FaceCorner(Direction.DOWN, LU), List.of(new Vector3f(1, 0, 1), new Vector3f(0.5f, 0, 1), new Vector3f(0.5f, 0, 0.5f), new Vector3f(1, 0, 0.5f)));
        map.put(new FaceCorner(Direction.DOWN, RU), List.of(new Vector3f(1, 0, 0.5f), new Vector3f(0.5f, 0, 0.5f), new Vector3f(0.5f, 0, 0), new Vector3f(1, 0, 0)));
        map.put(new FaceCorner(Direction.DOWN, LD), List.of(new Vector3f(0.5f, 0, 1), new Vector3f(0, 0, 1), new Vector3f(0, 0, 0.5f), new Vector3f(0.5f, 0, 0.5f)));
        map.put(new FaceCorner(Direction.DOWN, RD), List.of(new Vector3f(0.5f, 0, 0.5f), new Vector3f(0, 0, 0.5f), new Vector3f(0, 0, 0), new Vector3f(0.5f, 0, 0)));
        // spotless:on
        return map;
    }

    private void putVertex(
            QuadBakingVertexConsumer builder,
            TextureAtlasSprite sprite,
            Vec3i normal,
            float x,
            float y,
            float z,
            float u,
            float v) {
        builder.addVertex(x, y, z);
        builder.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        builder.setNormal((float) normal.getX(), (float) normal.getY(), (float) normal.getZ());
        u = sprite.getU(u);
        v = sprite.getV(v);
        builder.setUv(u, v);
    }

    private float getU0(int index) {
        return switch (index) {
            case 1, 3 -> 0.5f;
            default -> 0;
        };
    }

    private float getU1(int index) {
        return switch (index) {
            case 1, 3 -> 1;
            default -> 0.5f;
        };
    }

    private float getV0(int index) {
        return switch (index) {
            case 2, 3 -> 0.5f;
            default -> 0;
        };
    }

    private float getV1(int index) {
        return switch (index) {
            case 2, 3 -> 1;
            default -> 0.5f;
        };
    }

    public static class Connect {

        private final boolean[][][] connects = new boolean[3][3][3];
        private int face;

        int getFace(Direction face) {
            if (blocked(face)) {
                return -1;
            }
            return this.face;
        }

        void init(BlockPos pos) {
            this.face = Math.abs((pos.getX() ^ pos.getY() ^ pos.getZ()) % 3);
        }

        void set(int x, int y, int z) {
            this.connects[x + 1][y + 1][z + 1] = true;
        }

        int getIndex(Direction face, int corner) {
            if (blocked(face)) {
                return -1;
            }
            return switch (face) {
                case WEST, EAST: {
                    yield getIndexX(face, corner);
                }
                case DOWN, UP: {
                    yield getIndexY(face, corner);
                }
                case NORTH, SOUTH: {
                    yield getIndexZ(face, corner);
                }
            };
        }

        boolean blocked(Direction face) {
            var pos = face.getNormal().offset(1, 1, 1);
            return this.connects[pos.getX()][pos.getY()][pos.getZ()];
        }

        int getIndexX(Direction face, int corner) {
            int x = face.getStepX();
            return switch (corner) {
                case LU -> getIndex(this.connects[1][1][1 + x], this.connects[1][2][1], this.connects[1][2][1 + x]);
                case RU -> getIndex(this.connects[1][1][1 - x], this.connects[1][2][1], this.connects[1][2][1 - x]);
                case LD -> getIndex(this.connects[1][1][1 + x], this.connects[1][0][1], this.connects[1][0][1 + x]);
                case RD -> getIndex(this.connects[1][1][1 - x], this.connects[1][0][1], this.connects[1][0][1 - x]);
                default -> -1;
            };
        }

        int getIndexZ(Direction face, int corner) {
            int z = face.getStepZ();
            return switch (corner) {
                case LU -> getIndex(this.connects[1 - z][1][1], this.connects[1][2][1], this.connects[1 - z][2][1]);
                case RU -> getIndex(this.connects[1 + z][1][1], this.connects[1][2][1], this.connects[1 + z][2][1]);
                case LD -> getIndex(this.connects[1 - z][1][1], this.connects[1][0][1], this.connects[1 - z][0][1]);
                case RD -> getIndex(this.connects[1 + z][1][1], this.connects[1][0][1], this.connects[1 + z][0][1]);
                default -> -1;
            };
        }

        int getIndexY(Direction face, int corner) {
            int y = face.getStepY();
            return switch (corner) {
                case LU -> getIndex(this.connects[1][1][2], this.connects[1 - y][1][1], this.connects[1 - y][1][2]);
                case RU -> getIndex(this.connects[1][1][0], this.connects[1 - y][1][1], this.connects[1 - y][1][0]);
                case LD -> getIndex(this.connects[1][1][2], this.connects[1 + y][1][1], this.connects[1 + y][1][2]);
                case RD -> getIndex(this.connects[1][1][0], this.connects[1 + y][1][1], this.connects[1 + y][1][0]);
                default -> -1;
            };
        }

        /**
         * cbc <br>
         * axa <br>
         * cbc <br>
         */
        @SuppressWarnings("ConstantValue")
        int getIndex(boolean a, boolean b, boolean c) {
            if (!a && !b) {
                return 0;
            }
            if (a && b && !c) {
                return 1;
            }
            if (!a && b) {
                return 2;
            }
            if (a && !b) {
                return 3;
            }
            return -1;
        }
    }

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
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return this.sides;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @ParametersAreNonnullByDefault
    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return RENDER_TYPES;
    }

    private float getNormalStep(int step) {
        return step > 0 ? 0.001f : step < 0 ? -0.001f : 0;
    }

    private record FaceCorner(Direction face, int corner) {}
}
