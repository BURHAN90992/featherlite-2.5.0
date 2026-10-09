package com.featherlite.mixin;

import com.featherlite.Features;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** CameraFallbackMixin with raw intermediary names (works even without remapping data). */
@Mixin(Camera.class)
public abstract class CameraFallbackNMixin {
    @Shadow(remap = false) protected abstract void method_19325(float yaw, float pitch);
    @Shadow(remap = false) protected abstract void method_19327(double x, double y, double z);
    @Shadow(remap = false) protected abstract void method_19324(float surge, float heave, float sway);
    @Shadow(remap = false) protected abstract float method_19318(float distance);
    @Shadow(remap = false) private float field_18721;
    @Shadow(remap = false) private float field_18722;

    @Inject(method = "method_19321", at = @At("TAIL"), remap = false)
    private void featherlite$orbit(CallbackInfo ci) {
        if (!Features.needFallback()) return;
        Camera self = (Camera) (Object) this;
        Entity e = self.getFocusedEntity();
        if (e == null || e != MinecraftClient.getInstance().player) return;
        Features.hook("camera-orbit");
        this.method_19325(Features.flYaw, Features.flPitch);
        if (self.isThirdPerson()) {
            float tp = self.getLastTickProgress();
            Vec3d p = e.getLerpedPos(tp);
            this.method_19327(p.x, p.y + MathHelper.lerp(tp, this.field_18722, this.field_18721), p.z);
            this.method_19324(-this.method_19318(4.0F), 0.0F, 0.0F);
        }
    }
}
