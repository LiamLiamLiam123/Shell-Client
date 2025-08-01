package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.MinecraftClient;

public class ToggleSprint extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public ToggleSprint() {
        super("ToggleSprint", "vroom vroom...", Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player != null) {
            mc.options.sprintKey.setPressed(true);
        }
    }

    @Override
    public void onDisable() {
        mc.options.sprintKey.setPressed(false);
        super.onDisable();
    }
}

