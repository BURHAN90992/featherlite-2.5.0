package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Proves the mixin system works (constructor name needs no mapping). */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void featherlite$loaded(CallbackInfo ci) {
        Features.mixinsLoaded = true;
    }
}
