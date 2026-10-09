package com.featherlite;

import com.featherlite.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

/** Logic for Freelook, Zoom and Toggle Sprint (no HUD). */
public final class Features {
    public static Module freelook, zoom, sprint;

    // ---- freelook state (read by the mixins) ----
    public static boolean freelookActive;
    public static float flYaw, flPitch;
    /** True only while Camera.update runs. */
    public static boolean inCamera;
    /** True once vanilla read our freelook angles during this camera update. */
    public static boolean yawUsed;
    /** Set by a mixin on MinecraftClient: proves the mixin system loaded. */
    public static boolean mixinsLoaded;

    /** The player's real aim, frozen while freelook is active. */
    private static float realYaw, realPitch;
    private static final Set<String> HOOKS = new HashSet<>();

    private static Perspective savedPerspective;
    private static Integer savedFov;
    private static boolean autoSprint;

    private Features() {}

    // ---------- hook helpers (called from mixins) ----------
    public static void hook(String name) {
        if (HOOKS.add(name)) System.out.println("[FeatherLite] Freelook hook active: " + name);
    }

    public static boolean hooked(String name) { return HOOKS.contains(name); }

    private static boolean isPlayer(Object e) {
        return e == MinecraftClient.getInstance().player;
    }

    /** Mouse movement while freelook is active. Returns true if it was used (cancel vanilla turning). */
    public static boolean onMouse(Object entity, double dx, double dy) {
        if (!freelookActive || !isPlayer(entity)) return false;
        hook("mouse");
        flYaw += (float) dx * 0.15f;
        flPitch = MathHelper.clamp(flPitch + (float) dy * 0.15f, -90.0f, 90.0f);
        return true;
    }

    public static void cameraHead() {
        inCamera = true;
        yawUsed = false;
        if (freelookActive) {
            hook("camera-update");
            absorbMouse();
        }
    }

    public static void cameraTail() {
        inCamera = false;
    }

    /** True if the camera should read the freelook angles for this entity. */
    public static boolean wantAngle(Object entity) {
        return inCamera && freelookActive && isPlayer(entity);
    }

    public static boolean needFallback() {
        return freelookActive && !yawUsed;
    }

    /**
     * Fallback if the mouse hook could not be applied: the mouse turned the
     * player, so move that turn into the camera and restore the real aim.
     */
    private static void absorbMouse() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        float dy = mc.player.getYaw() - realYaw;
        float dp = mc.player.getPitch() - realPitch;
        if (dy == 0f && dp == 0f) return;
        hook("mouse-absorb");
        flYaw += dy;
        flPitch = MathHelper.clamp(flPitch + dp, -90f, 90f);
        mc.player.setYaw(realYaw);
        mc.player.setPitch(realPitch);
    }

    // ---------- per-tick logic ----------
    public static void tick(MinecraftClient mc) {
        boolean inGame = mc.player != null && mc.currentScreen == null;

        // Freelook (hold key)
        boolean wantLook = inGame && freelook.enabled && down(mc, freelook.keyCode);
        if (wantLook && !freelookActive) {
            flYaw = realYaw = mc.player.getYaw();
            flPitch = realPitch = mc.player.getPitch();
            freelookActive = true;
            if (freelook.optThird) {
                savedPerspective = mc.options.getPerspective();
                mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
            }
        } else if (!wantLook && freelookActive) {
            stopFreelook(mc);
        }

        // Zoom (hold key)
        boolean wantZoom = inGame && zoom.enabled && down(mc, zoom.keyCode);
        if (wantZoom && savedFov == null) {
            savedFov = mc.options.getFov().getValue();
            mc.options.getFov().setValue(30);
        } else if (!wantZoom && savedFov != null) {
            stopZoom(mc);
        }

        // Toggle sprint
        if (inGame && sprint.enabled) {
            boolean walking = mc.options.forwardKey.isPressed() && !mc.options.sneakKey.isPressed();
            if (walking) {
                mc.options.sprintKey.setPressed(true);
                autoSprint = true;
            } else if (autoSprint) {
                mc.options.sprintKey.setPressed(false);
                autoSprint = false;
            }
        } else if (autoSprint) {
            mc.options.sprintKey.setPressed(false);
            autoSprint = false;
        }
    }

    private static boolean down(MinecraftClient mc, int key) {
        return key > 0 && GLFW.glfwGetKey(mc.getWindow().getHandle(), key) == GLFW.GLFW_PRESS;
    }

    private static void stopFreelook(MinecraftClient mc) {
        freelookActive = false;
        if (savedPerspective != null) mc.options.setPerspective(savedPerspective);
        savedPerspective = null;
    }

    private static void stopZoom(MinecraftClient mc) {
        mc.options.getFov().setValue(savedFov);
        savedFov = null;
    }

    /** Restore options before the game closes. */
    public static void shutdown(MinecraftClient mc) {
        if (freelookActive) stopFreelook(mc);
        if (savedFov != null) stopZoom(mc);
    }
}
