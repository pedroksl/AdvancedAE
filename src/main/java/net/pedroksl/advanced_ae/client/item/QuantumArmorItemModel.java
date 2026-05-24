package net.pedroksl.advanced_ae.client.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.joml.Matrix4fc;
import org.jspecify.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.ae2addonlib.util.Colors;

import appeng.client.render.ItemBaseModelWrapper;

public class QuantumArmorItemModel implements ItemModel {
    private final ItemBaseModelWrapper model;

    public QuantumArmorItemModel(ItemBaseModelWrapper model) {
        this.model = model;
    }

    @Override
    public void update(
            ItemStackRenderState renderState,
            ItemStack stack,
            ItemModelResolver itemModelResolver,
            ItemDisplayContext displayContext,
            @Nullable ClientLevel level,
            @Nullable ItemOwner owner,
            int seed) {
        if (!(stack.getItem() instanceof QuantumArmorBase armor)) {
            return;
        }

        renderState.appendModelIdentityElement(this);

        var layer = renderState.newLayer();

        var color = armor.getTintColor(stack);
        if (color == -1) {
            color = Colors.PURPLE.argb();
        }

        renderState.appendModelIdentityElement(color);
        var tint = layer.tintLayers();
        tint.add(-1);
        tint.add(ARGB.opaque(color));
        this.model.applyToLayer(layer, displayContext);
        renderState.appendModelIdentityElement(tint.getInt(1));
    }

    public record Unbaked(Identifier model) implements ItemModel.Unbaked {
        public static final Identifier ID = AdvancedAE.makeId("quantum_armor");

        public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(Identifier.CODEC.fieldOf("model").forGetter(Unbaked::model))
                        .apply(builder, Unbaked::new));

        @Override
        public MapCodec<? extends ItemModel.Unbaked> type() {
            return MAP_CODEC;
        }

        @Override
        public ItemModel bake(BakingContext bakingContext, Matrix4fc matrix4fc) {
            return new QuantumArmorItemModel(
                    ItemBaseModelWrapper.bake(bakingContext.blockModelBaker(), this.model, matrix4fc));
        }

        @Override
        public void resolveDependencies(Resolver resolver) {
            resolver.markDependency(model);
        }
    }
}
