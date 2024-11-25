package net.pedroksl.advanced_ae.mixins.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * Replicates AE2's own {@link appeng.mixins.ModelBakeryMixin} for models from the AAE namespace.
 */
@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {
    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void loadModelHook(ResourceLocation id, CallbackInfoReturnable<UnbakedModel> cir) {
        var model = BuiltInModelHooksAccessor.getBuiltInModels().get(id);

        if (model != null) {
            cir.setReturnValue(model);
        }
    }
}
