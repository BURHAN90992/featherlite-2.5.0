package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Only while the camera is built, so aiming and attacks keep using the real rotation. */
@Mixin(Entity.class)
public abstract class EntityAngleMixin {
    @Inject(method = "getYaw(F)F", at = @At("HEAD"), cancellable = true)
    private void featherlite$yaw(float tickProgress, CallbackInfoReturnable<Float> cir) {
        if (Features.wantAngle(this)) {
            Features.hook("camera-yaw");
            Features.yawUsed = true;
            cir.setReturnValue(Features.flYaw);
        }
    }

    @Inject(method = "getPitch(F)F", at = @At("HEAD"), cancellable = true)
    private void featherlite$pitch(float tickProgress, CallbackInfoReturnable<Float> cir) {
        if (Features.wantAngle(this)) {
            Features.hook("camera-pitch");
            cir.setReturnValue(Features.flPitch);
        }
    }
}
