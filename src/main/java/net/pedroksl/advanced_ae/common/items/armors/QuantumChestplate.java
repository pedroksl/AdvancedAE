package net.pedroksl.advanced_ae.common.items.armors;

import java.util.List;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.pedroksl.advanced_ae.common.definitions.AAEMaterials;

import software.bernie.geckolib.animatable.GeoItem;

public class QuantumChestplate extends QuantumArmorBase implements GeoItem{

    public QuantumChestplate(Properties properties) {
        super(AAEMaterials.QUANTUM_ALLOY.holder(), Type.CHESTPLATE, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(
            ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        //		Level level = context.level();
        //		if (level == null) {
        //			return;
        //		}
        //
        //		boolean sneakPressed = Screen.hasShiftDown();
        //		appendFEText(stack, tooltip);
        //		if (sneakPressed) {
        //			appendToolEnabled(stack, tooltip);
        //			appendAbilityList(stack, tooltip);
        //		} else {
        //			appendToolEnabled(stack, tooltip);
        //			appendShiftForInfo(stack, tooltip);
        //		}
    }

    @Override
    public void inventoryTick(
            @NotNull ItemStack itemStack,
            @NotNull Level level,
            @NotNull Entity entity,
            int itemSlot,
            boolean isSelected) {
        if (itemSlot == Inventory.INVENTORY_SIZE + EquipmentSlot.CHEST.getIndex() && entity instanceof Player player) {
            // tick
        }
    }

    @Override
    public <T extends LivingEntity> int damageItem(
            ItemStack stack, int amount, @Nullable T entity, Consumer<Item> onBroken) {
        IEnergyStorage energyStorage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyStorage == null) return amount;
        double reductionFactor = 0;
        if (entity != null) {
            HolderLookup.RegistryLookup<Enchantment> registrylookup =
                    entity.level().getServer().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            int unbreakingLevel = stack.getEnchantmentLevel(registrylookup.getOrThrow(Enchantments.UNBREAKING));
            reductionFactor = Math.min(1.0, unbreakingLevel * 0.1);
        }
        int finalEnergyCost = (int) Math.max(0, amount - (amount * reductionFactor));
        energyStorage.extractEnergy(finalEnergyCost, false);
        return 0;
    }
}
