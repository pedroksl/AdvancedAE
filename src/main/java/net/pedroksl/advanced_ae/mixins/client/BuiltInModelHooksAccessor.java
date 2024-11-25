package net.pedroksl.advanced_ae.mixins.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import appeng.hooks.BuiltInModelHooks;

@Mixin(BuiltInModelHooks.class)
public interface BuiltInModelHooksAccessor {
    @Accessor(value = "builtInModels", remap = false)
    static Map<ResourceLocation, UnbakedModel> getBuiltInModels() {
        throw new AssertionError();
    }
}
