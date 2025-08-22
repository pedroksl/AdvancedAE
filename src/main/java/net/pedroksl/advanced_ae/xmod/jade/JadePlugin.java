package net.pedroksl.advanced_ae.xmod.jade;

import net.minecraft.resources.ResourceLocation;
import net.pedroksl.advanced_ae.common.entities.ReactionChamberEntity;
import net.pedroksl.advanced_ae.common.logic.AdvPatternProviderLogicHost;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Plugin to remove the mekanism-added chemical handler lines for interfaces and pattern providers.
 */
@WailaPlugin
public class JadePlugin implements IWailaPlugin {
    private static final ResourceLocation[] CHEMICALS = {
        new ResourceLocation("mekanism", "gas"),
        new ResourceLocation("mekanism", "infuse_type"),
        new ResourceLocation("mekanism", "pigment"),
        new ResourceLocation("mekanism", "slurry"),
    };

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addTooltipCollectedCallback((tooltip, accessor) -> {
            var target = accessor.getTarget();

            if (target instanceof AdvPatternProviderLogicHost || target instanceof ReactionChamberEntity) {
                for (var loc : CHEMICALS) {
                    tooltip.remove(loc);
                }
            }
        });
    }
}
