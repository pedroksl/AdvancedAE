package net.pedroksl.advanced_ae.common.items.armors;

import java.util.List;
import java.util.function.DoubleSupplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.pedroksl.advanced_ae.common.helpers.PoweredItemCapabilities;

import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.core.localization.Tooltips;

public class PoweredItem extends ArmorItem implements IAEItemPowerStorage {

    private static final double MIN_POWER = 0.0001;
    private static final String CURRENT_POWER_NBT_KEY = "internalCurrentPower";
    private static final String MAX_POWER_NBT_KEY = "internalMaxPower";
    private final DoubleSupplier powerCapacity;
    private PoweredItemCapabilities cap;

    public PoweredItem(ArmorMaterial material, Type type, Properties properties, DoubleSupplier powerCapacity) {
        super(material, type, properties);
        this.powerCapacity = powerCapacity;
    }

    public void addToMainCreativeTab(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        output.accept(this);

        var charged = new ItemStack(this, 1);
        injectAEPower(charged, getAEMaxPower(charged), Actionable.MODULATE);
        output.accept(charged);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(
            ItemStack stack, @NotNull Level context, List<Component> lines, TooltipFlag advancedTooltips) {
        var storedEnergy = getAECurrentPower(stack);
        var energyCapacity = getAEMaxPower(stack);
        lines.add(Tooltips.energyStorageComponent(storedEnergy, energyCapacity));
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return this.getAECurrentPower(stack) / this.getAEMaxPower(stack) < 0.99f;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        double filled = getAECurrentPower(stack) / getAEMaxPower(stack);
        return Mth.clamp((int) (filled * 13), 0, 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        // This is the standard green color of full durability bars
        return Mth.hsvToRgb(1 / 3.0F, 1.0F, 1.0F);
    }

    @Override
    public double injectAEPower(ItemStack stack, double amount, Actionable mode) {
        final double maxStorage = this.getAEMaxPower(stack);
        final double currentStorage = this.getAECurrentPower(stack);
        final double required = maxStorage - currentStorage;
        final double overflow = Math.max(0, Math.min(amount - required, amount));

        if (mode == Actionable.MODULATE) {
            var toAdd = Math.min(amount, required);
            setAECurrentPower(stack, currentStorage + toAdd);
        }

        return overflow;
    }

    @Override
    public double extractAEPower(ItemStack stack, double amount, Actionable mode) {
        final double currentStorage = this.getAECurrentPower(stack);
        final double fulfillable = Math.min(amount, currentStorage);

        if (mode == Actionable.MODULATE) {
            setAECurrentPower(stack, currentStorage - fulfillable);
        }

        return fulfillable;
    }

    @Override
    public double getAEMaxPower(ItemStack stack) {
        // Allow per-item-stack overrides of the maximum power storage
        var tag = stack.getTag();
        if (tag != null && tag.contains(MAX_POWER_NBT_KEY, Tag.TAG_DOUBLE)) {
            return tag.getDouble(MAX_POWER_NBT_KEY);
        }

        return this.powerCapacity.getAsDouble();
    }

    protected final void setAEMaxPower(ItemStack stack, double maxPower) {
        var defaultCapacity = powerCapacity.getAsDouble();
        if (Math.abs(maxPower - defaultCapacity) < MIN_POWER) {
            stack.removeTagKey(MAX_POWER_NBT_KEY);
            maxPower = defaultCapacity;
        } else {
            stack.getOrCreateTag().putDouble(MAX_POWER_NBT_KEY, maxPower);
        }

        // Clamp current power to be within bounds
        var currentPower = getAECurrentPower(stack);
        if (currentPower > maxPower) {
            setAECurrentPower(stack, maxPower);
        }
    }

    protected final void setAEMaxPowerMultiplier(ItemStack stack, int multiplier) {
        multiplier = Mth.clamp(multiplier, 1, 100);
        setAEMaxPower(stack, multiplier * powerCapacity.getAsDouble());
    }

    protected final void resetAEMaxPower(ItemStack stack) {
        setAEMaxPower(stack, powerCapacity.getAsDouble());
    }

    @Override
    public double getAECurrentPower(ItemStack is) {
        var tag = is.getTag();
        if (tag != null) {
            return tag.getDouble(CURRENT_POWER_NBT_KEY);
        } else {
            return 0;
        }
    }

    protected final void setAECurrentPower(ItemStack stack, double power) {
        if (power < MIN_POWER) {
            stack.removeTagKey(CURRENT_POWER_NBT_KEY);
        } else {
            stack.getOrCreateTag().putDouble(CURRENT_POWER_NBT_KEY, power);
        }
    }

    @Override
    public AccessRestriction getPowerFlow(ItemStack is) {
        return AccessRestriction.WRITE;
    }

    @Override
    public double getChargeRate(ItemStack itemStack) {
        return 20000d;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new PoweredItemCapabilities(stack, this);
    }
}
