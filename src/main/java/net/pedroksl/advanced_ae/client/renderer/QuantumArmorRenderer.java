package net.pedroksl.advanced_ae.client.renderer;

import java.util.HashMap;
import java.util.Map;

import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.DataTickets;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.base.BoneSnapshots;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.GeoRenderer;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer;
import com.geckolib.renderer.layer.builtin.TextureLayerGeoLayer;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;
import net.pedroksl.advanced_ae.xmod.iris.IrisPlugin;
import net.pedroksl.ae2addonlib.util.Colors;

public class QuantumArmorRenderer<R extends HumanoidRenderState & GeoRenderState>
        extends GeoArmorRenderer<QuantumArmorBase, R> {

    public static final DataTicket<Integer> TINT_COLOR = DataTicket.create("quantum_armor_tint_color", Integer.class);

    public enum Bones {
        HEAD("armorHead"),
        CHEST("armorBody"),
        LEFT_ARM("armorLeftArm"),
        RIGHT_ARM("armorRightArm"),
        LEFT_LEGS("armorLeftLeg"),
        RIGHT_LEGS("armorRightLeg"),
        LEFT_FOOT("armorLeftBoot"),
        RIGHT_FOOT("armorRightBoot"),
        HUD("hud"),
        LEFT_BLADE("blade_left"),
        RIGHT_BLADE("blade_right");

        public final String boneName;

        Bones(String boneName) {
            this.boneName = boneName;
        }
    }

    private final Map<Bones, Boolean> visibilityMap = new HashMap<>();

    public QuantumArmorRenderer() {
        super(new DefaultedItemGeoModel<>(AdvancedAE.makeId("quantum_armor")));

        withRenderLayer(new QuantumArmorTintLayer<>(this));
        withRenderLayer(new AutoGlowingGeoLayer<>(this));
        withRenderLayer(new QuantumArmorTintGlowingLayer<>(this));
    }

    public void setBoneVisible(Bones bone, boolean visible) {
        visibilityMap.put(bone, visible);
    }

    @Override
    public void addRenderData(
            QuantumArmorBase animatable, @Nullable RenderData relatedObject, R renderState, float partialTick) {
        if (relatedObject == null) return;

        var stack = relatedObject.itemStack();
        var slot = relatedObject.slot();

        renderState.addGeckolibData(TINT_COLOR, animatable.getTintColor(stack));

        setVisible(slot, animatable.isVisible(stack));
        toggleBoneVisibilities(slot, stack);
    }

    private void toggleBoneVisibilities(EquipmentSlot slot, ItemStack stack) {
        if (slot == EquipmentSlot.HEAD) {
            var isHudVisible = IrisPlugin.isShaderPackInUse();
            setBoneVisible(Bones.HUD, isHudVisible);
        } else if (slot == EquipmentSlot.CHEST) {
            var areBladesVisible = stack.has(AAEComponents.UPGRADE_TOGGLE.get(UpgradeType.STRENGTH));
            setBoneVisible(Bones.LEFT_BLADE, areBladesVisible);
            setBoneVisible(Bones.RIGHT_BLADE, areBladesVisible);
        }
    }

    private void setVisible(EquipmentSlot slot, boolean visible) {
        switch (slot) {
            case HEAD -> visibilityMap.put(Bones.HEAD, visible);
            case CHEST -> {
                visibilityMap.put(Bones.CHEST, visible);
                visibilityMap.put(Bones.LEFT_ARM, visible);
                visibilityMap.put(Bones.RIGHT_ARM, visible);
            }
            case LEGS -> {
                visibilityMap.put(Bones.LEFT_LEGS, visible);
                visibilityMap.put(Bones.RIGHT_LEGS, visible);
            }
            case FEET -> {
                visibilityMap.put(Bones.LEFT_FOOT, visible);
                visibilityMap.put(Bones.RIGHT_FOOT, visible);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void adjustModelBonesForRender(RenderPassInfo<R> renderPassInfo, BoneSnapshots snapshots) {
        super.adjustModelBonesForRender(renderPassInfo, snapshots);

        for (var entry : visibilityMap.entrySet()) {
            snapshots.ifPresent(entry.getKey().boneName, snapshot -> {
                if (!entry.getValue()) {
                    snapshot.skipRender(true);
                    snapshot.skipChildrenRender(true);
                }
            });
        }
    }

    private static class QuantumArmorTintLayer<T extends GeoAnimatable, O, R extends GeoRenderState>
            extends TextureLayerGeoLayer<T, O, R> {

        public QuantumArmorTintLayer(GeoRenderer<T, O, R> renderer) {
            super(renderer, AdvancedAE.makeId("textures/item/quantum_armor_tint.png"));
        }

        @Override
        public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
            var color = renderPassInfo.renderColor();

            renderPassInfo
                    .renderState()
                    .addGeckolibData(
                            DataTickets.RENDER_COLOR,
                            renderPassInfo.renderState().getOrDefaultGeckolibData(TINT_COLOR, Colors.PURPLE.argb()));
            super.submitRenderTask(renderPassInfo, renderTasks);
            renderPassInfo.renderState().addGeckolibData(DataTickets.RENDER_COLOR, color);
        }
    }

    private static class QuantumArmorTintGlowingLayer<T extends GeoAnimatable, O, R extends GeoRenderState>
            extends AutoGlowingGeoLayer<T, O, R> {

        private static final Identifier TEXTURE = AdvancedAE.makeId("textures/item/quantum_armor_tint_glowmask.png");

        public QuantumArmorTintGlowingLayer(GeoRenderer<T, O, R> renderer) {
            super(renderer);
        }

        @Override
        protected Identifier getTextureResource(R renderState) {
            return TEXTURE;
        }

        @Override
        public void submitRenderTask(RenderPassInfo<R> renderPassInfo, SubmitNodeCollector renderTasks) {
            var color = renderPassInfo.renderColor();

            renderPassInfo
                    .renderState()
                    .addGeckolibData(
                            DataTickets.RENDER_COLOR,
                            renderPassInfo.renderState().getOrDefaultGeckolibData(TINT_COLOR, Colors.PURPLE.argb()));
            super.submitRenderTask(renderPassInfo, renderTasks);
            renderPassInfo.renderState().addGeckolibData(DataTickets.RENDER_COLOR, color);
        }
    }
}
