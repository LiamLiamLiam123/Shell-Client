package me.alpha432.oyvey.features.modules.movement;

import me.alpha432.oyvey.features.modules.Module;
import net.minecraft.client.Minecraft;

public class ToggleSprint extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();

    public ToggleSprint() {
        super("ToggleSprint", "vroom vroom...", Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onUpdate() {
        mc.gameSettings.keyBindSprint.setPressed(true);
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindSprint.setPressed(false);
        super.onDisable();
    }
}
