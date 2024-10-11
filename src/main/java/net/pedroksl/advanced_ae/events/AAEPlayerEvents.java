package net.pedroksl.advanced_ae.events;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.pedroksl.advanced_ae.AdvancedAE;
import net.pedroksl.advanced_ae.common.definitions.AAEComponents;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;
import net.pedroksl.advanced_ae.common.items.upgrades.UpgradeType;

public class AAEPlayerEvents {
    public static final AttributeModifier flight =
            new AttributeModifier(AdvancedAE.makeId("flight"), 1.0, AttributeModifier.Operation.ADD_VALUE);

    @SubscribeEvent
    public static void ItemAttributes(ItemAttributeModifierEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof QuantumArmorBase armor) {
            if (armor.isUpgradeEnabledAndPowered(itemStack, UpgradeType.STEP_ASSIST)) {
                int value = itemStack.getOrDefault(AAEComponents.UPGRADE_VALUE.get(UpgradeType.STEP_ASSIST), 1);
                event.addModifier(Attributes.STEP_HEIGHT, getStepAssist(value), EquipmentSlotGroup.FEET);
            }
            if (armor.isUpgradeEnabledAndPowered(itemStack, UpgradeType.FLIGHT))
                event.addModifier(NeoForgeMod.CREATIVE_FLIGHT, flight, EquipmentSlotGroup.CHEST);
        }
    }

    private static AttributeModifier getStepAssist(int value) {
        return new AttributeModifier(AdvancedAE.makeId("step_assist"), value, AttributeModifier.Operation.ADD_VALUE);
    }
}
