package net.pedroksl.advanced_ae;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(value = AdvancedAE.MOD_ID, dist = Dist.DEDICATED_SERVER)
public class AdvancedAEServer extends AdvancedAE {

    public AdvancedAEServer(IEventBus eventBus, ModContainer container) {
        super(eventBus, container);
    }

    @Override
    @Nullable
    public Level getClientLevel() {
        return null;
    }

    @Override
    public void registerHotkey(String id) {}
}
