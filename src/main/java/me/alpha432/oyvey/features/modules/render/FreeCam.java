package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class FreeCam extends Module {

    private Entity freecamEntity;
    private Vec3d originalPosition;
    private float originalYaw;
    private float originalPitch;

    private final MinecraftClient mc = MinecraftClient.getInstance();

    public FreeCam() {
        super("FreeCam", "Allows you to fly around freely without moving your real player", Category.RENDER, false, false, false);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) return;

        // Store original player data
        originalPosition = mc.player.getPos();
        originalYaw = mc.player.getYaw();
        originalPitch = mc.player.getPitch();

        // Clone the player for rendering
        ClientPlayerEntity clone = new ClientPlayerEntity(mc, (ClientWorld) mc.world, mc.player.getGameProfile(), null);
        clone.copyPositionAndRotation(mc.player);
        clone.setYaw(mc.player.getYaw());
        clone.setPitch(mc.player.getPitch());

        freecamEntity = clone;
        mc.world.addEntity(-69420, freecamEntity); // Negative ID to avoid conflict
        mc.setCameraEntity(freecamEntity);
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) return;

        // Remove the freecam entity and restore control
        mc.setCameraEntity(mc.player);
        if (freecamEntity != null) {
            mc.world.removeEntity(freecamEntity);
            freecamEntity = null;
        }

        // Restore original player position and rotation
        mc.player.setPosition(originalPosition);
        mc.player.setYaw(originalYaw);
        mc.player.setPitch(originalPitch);
    }

    @Override
    public void onTick() {
        if (freecamEntity != null && mc.player != null) {
            handleFreecamMovement(freecamEntity);
        }
    }

    private void handleFreecamMovement(Entity entity) {
        Vec3d velocity = Vec3d.ZERO;

        if (mc.options.forwardKey.isPressed()) {
            velocity = velocity.add(entity.getRotationVec(1.0F).multiply(0.5));
        }
        if (mc.options.backKey.isPressed()) {
            velocity = velocity.add(entity.getRotationVec(1.0F).multiply(-0.5));
        }
        if (mc.options.leftKey.isPressed()) {
            velocity = velocity.add(entity.getRotationVec(1.0F).rotateY((float)Math.toRadians(90)).multiply(0.5));
        }
        if (mc.options.rightKey.isPressed()) {
            velocity = velocity.add(entity.getRotationVec(1.0F).rotateY((float)Math.toRadians(-90)).multiply(0.5));
        }
        if (mc.options.jumpKey.isPressed()) {
            velocity = velocity.add(0, 0.5, 0);
        }
        if (mc.options.sneakKey.isPressed()) {
            velocity = velocity.add(0, -0.5, 0);
        }

        entity.updatePosition(
            entity.getX() + velocity.x,
            entity.getY() + velocity.y,
            entity.getZ() + velocity.z
        );
    }
}
