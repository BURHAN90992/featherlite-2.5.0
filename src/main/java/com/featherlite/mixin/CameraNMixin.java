package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Same as CameraMixin, but with raw intermediary names in case remapping data is missing. */
@Mixin(Camera.class)
public abstract class CameraNMixin {
    @Inject(method = "method_19321", at = @At("HEAD"), remap = false)
    private void featherlite$head(CallbackInfo ci) { Features.cameraHead(); }

    @Inject(method = "method_19321", at = @At("TAIL"), remap = false)
    private void featherlite$tail(CallbackInfo ci) { Features.cameraTail(); }
}
