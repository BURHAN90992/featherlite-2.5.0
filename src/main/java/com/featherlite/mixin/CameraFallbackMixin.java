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

/**
 * Freelook camera. After vanilla builds the camera, rebuild it from the freelook
 * angles the same way vanilla does: rotation, then the player's eye position,
 * then (third person) move back along the new view direction. That makes the
 * camera orbit AROUND THE PLAYER instead of spinning in place.
 */
@Mixin(Camera.class)
public abstract class CameraFallbackMixin {
    @Shadow protected abstract void setRotation(float yaw, float pitch);
    @Shadow protected abstract void setPos(double x, double y, double z);
    @Shadow protected abstract void moveBy(float surge, float heave, float sway);
    @Shadow protected abstract float clipToSpace(float distance);
    @Shadow private float cameraY;
    @Shadow private float lastCameraY;

    @Inject(method = "update", at = @At("TAIL"))
    private void featherlite$orbit(CallbackInfo ci) {
        if (!Features.needFallback()) return;
        Camera self = (Camera) (Object) this;
        Entity e = self.getFocusedEntity();
        if (e == null || e != MinecraftClient.getInstance().player) return;
        Features.hook("camera-orbit");
        this.setRotation(Features.flYaw, Features.flPitch);
        if (self.isThirdPerson()) {
            float tp = self.getLastTickProgress();
            Vec3d p = e.getLerpedPos(tp);
            this.setPos(p.x, p.y + MathHelper.lerp(tp, this.lastCameraY, this.cameraY), p.z);
            this.moveBy(-this.clipToSpace(4.0F), 0.0F, 0.0F);
        }
    }
}
