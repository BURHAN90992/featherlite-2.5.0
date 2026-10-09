package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Freelook: the mouse turns the camera, not the player. */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void featherlite$mouse(double dx, double dy, CallbackInfo ci) {
        if (Features.onMouse(this, dx, dy)) ci.cancel();
    }
}
