package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** EntityMixin with raw intermediary names. */
@Mixin(Entity.class)
public abstract class EntityNMixin {
    @Inject(method = "method_5872", at = @At("HEAD"), cancellable = true, remap = false)
    private void featherlite$mouse(double dx, double dy, CallbackInfo ci) {
        if (Features.onMouse(this, dx, dy)) ci.cancel();
    }
}
