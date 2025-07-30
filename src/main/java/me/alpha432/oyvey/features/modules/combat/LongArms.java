package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;

public class LongArms extends Module {
    public final Setting<Double> reach = this.register(new Setting<>("Reach", 4.5, 3.0, 10.0));
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public LongArms() {
        super("LongArms", "Increases attack reach.", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        Entity target = mc.crosshairTarget instanceof net.minecraft.util.hit.EntityHitResult ehr ? ehr.getEntity() : null;
        if (target instanceof PlayerEntity || target != null) {
            double allowed = reach.getValue();
            double distSq = mc.player.squaredDistanceTo(target);
            if (distSq <= allowed * allowed) {
                mc.interactionManager.attackEntity(mc.player, target);
                mc.player.swingHand(Hand.MAIN_HAND);
            }
        }
    }

    public double getReach() {
        return reach.getValue();
    }
}
