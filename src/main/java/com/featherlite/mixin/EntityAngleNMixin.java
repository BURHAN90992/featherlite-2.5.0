package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** EntityAngleMixin with raw intermediary names. */
@Mixin(Entity.class)
public abstract class EntityAngleNMixin {
    @Inject(method = "method_5705(F)F", at = @At("HEAD"), cancellable = true, remap = false)
    private void featherlite$yaw(float tickProgress, CallbackInfoReturnable<Float> cir) {
        if (Features.wantAngle(this)) {
            Features.hook("camera-yaw");
            Features.yawUsed = true;
            cir.setReturnValue(Features.flYaw);
        }
    }

    @Inject(method = "method_5695(F)F", at = @At("HEAD"), cancellable = true, remap = false)
    private void featherlite$pitch(float tickProgress, CallbackInfoReturnable<Float> cir) {
        if (Features.wantAngle(this)) {
            Features.hook("camera-pitch");
            cir.setReturnValue(Features.flPitch);
        }
    }
}
