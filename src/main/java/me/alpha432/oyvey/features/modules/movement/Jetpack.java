package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;

public class Jetpack extends Module {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final Timer timer = new Timer();

    public Jetpack() {
        super("Jetpack", "Bypasses anti-cheat with slow vertical boosts", Category.MOVEMENT, false, false, false);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        if (!mc.player.isOnGround() && mc.options.jumpKey.isPressed()) {
            if (timer.passedMs(300)) { // delay boosts to mimic legit movement
                Vec3d velocity = mc.player.getVelocity();

                // Apply soft Y boost (not too fast, avoid flagging)
                mc.player.setVelocity(velocity.x, Math.min(velocity.y + 0.15, 0.5), velocity.z);

                // Optional: stop fall damage detection
                mc.player.setOnGround(true);

                timer.reset();
            }
        }
    }
}
