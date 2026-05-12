package net.pedroksl.advanced_ae.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.pedroksl.advanced_ae.common.items.armors.QuantumArmorBase;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity extends Entity {

    @Shadow
    protected float lastHurt;

    @Inject(method = "playHurtSound", at = @At("HEAD"), cancellable = true)
    public void hurtSound(DamageSource source, CallbackInfo ci) {
        if (lastHurt < 10f) {
            if (source.getEntity() != null && super.getType() == EntityType.PLAYER) {
                for (var slot : EquipmentSlot.values()) {
                    if (((Player) source.getEntity()).getItemBySlot(slot).getItem() instanceof QuantumArmorBase) {
                        ci.cancel();
                        return;
                    }
                }
            }
        }
    }

    protected MixinLivingEntity(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }
}
