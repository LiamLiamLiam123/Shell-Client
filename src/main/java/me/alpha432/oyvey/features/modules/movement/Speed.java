package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.entity.attribute.EntityAttributes;

public class Speed extends Module {
    private final Setting<Float> speed = num("Speed", 1.0f, 0.1f, 5.0f);
    
    public Speed() {
        super("Speed", "Increases your movement speed", Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (nullCheck()) return;

        PlayerEntity player = mc.player;

        if (player.isOnGround() && player.forwardSpeed != 0) {
            double yaw = Math.toRadians(player.getYaw());
            double x = -Math.sin(yaw) * speed.getValue();
            double z = Math.cos(yaw) * speed.getValue();
            player.setVelocity(new Vec3d(x, player.getVelocity().y, z));
        }
    }
}
