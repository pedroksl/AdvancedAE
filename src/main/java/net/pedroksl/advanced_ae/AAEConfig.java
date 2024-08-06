package net.pedroksl.advanced_ae;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = AdvancedAE.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AAEConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	private static final ForgeConfigSpec.IntValue ADV_BUS_SPEED = BUILDER
			.comment("ME Advanced Import/Export Bus speed multiplier")
			.defineInRange("exBusMultiplier", 8, 2, 128);

	static final ForgeConfigSpec SPEC = BUILDER.build();

	public static int busSpeed;
	public static List<ResourceLocation> tapeWhitelist;

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		busSpeed = ADV_BUS_SPEED.get();
	}
}
