package me.alpha432.oyvey.features.modules.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;

public class Fullbright {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private float previousGamma = 0.0f;
    private boolean enabled = false;

    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }

    public void enable() {
        if (enabled) return;

        GameOptions options = mc.options;
        previousGamma = options.getGamma().getValue();  // Save previous gamma (Fabric 1.19+ uses Option types)
        options.getGamma().setValue(1000.0f);           // Set gamma to a very high value
        enabled = true;
    }

    public void disable() {
        if (!enabled) return;

        mc.options.getGamma().setValue(previousGamma); // Restore previous gamma
        enabled = false;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
