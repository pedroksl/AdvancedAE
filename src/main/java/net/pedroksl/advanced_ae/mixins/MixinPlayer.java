package net.pedroksl.advanced_ae.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    public void hurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> ci) {
        if (this.lastHurt < 10f) {
            for (var slot : EquipmentSlot.values()) {
                if (getItemBySlot(slot).getItem() instanceof QuantumArmorBase) {
                    ci.setReturnValue(null);
                    ci.cancel();
                    return;
                }
            }
        }
    }

    protected MixinPlayer(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
}
