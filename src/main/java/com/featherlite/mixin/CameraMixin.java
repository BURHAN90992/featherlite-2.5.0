package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Freelook: while Camera.update runs, the player reports the freelook angles. */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Inject(method = "update", at = @At("HEAD"))
    private void featherlite$head(CallbackInfo ci) { Features.cameraHead(); }

    @Inject(method = "update", at = @At("TAIL"))
    private void featherlite$tail(CallbackInfo ci) { Features.cameraTail(); }
}
