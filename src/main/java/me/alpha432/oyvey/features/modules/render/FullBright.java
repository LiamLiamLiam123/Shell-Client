package me.alpha432.oyvey.features.modules.render;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.setting.Setting;
import net.minecraft.client.MinecraftClient;

public class Fullbright extends Module {
    public Setting<Double> gamma = register(new Setting<>("Gamma", 15.0, 1.0, 1000.0));

    private double previousGamma = -1;

    public Fullbright() {
        super("Fullbright", "Makes the game fully bright with customizable gamma", Category.RENDER, true, false, false);
    }

    @Override
    public void onEnable() {
        if (mc != null && mc.options != null) {
            previousGamma = mc.options.getGamma().getValue();
            mc.options.getGamma().setValue(gamma.getValue());
        }
    }

    @Override
    public void onDisable() {
        if (mc != null && mc.options != null && previousGamma != -1) {
            mc.options.getGamma().setValue(previousGamma);
        }
    }

    @Override
    public void onTick() {
        if (isEnabled() && mc != null && mc.options != null) {
            mc.options.getGamma().setValue(gamma.getValue());
        }
    }
}
