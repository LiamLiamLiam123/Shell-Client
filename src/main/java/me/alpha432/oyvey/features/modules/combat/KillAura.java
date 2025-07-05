package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {

    public Setting<Float> range = register(new Setting<>("Range", 4.5f, 1.0f, 6.0f));
    public Setting<Boolean> onlyPlayers = register(new Setting<>("OnlyPlayers", true));
    public Setting<Boolean> lineOfSight = register(new Setting<>("LineOfSight", true));
    public Setting<Integer> cps = register(new Setting<>("CPS", 8, 1, 20));

    private final Timer timer = new Timer();

    public KillAura() {
        super("KillAura", "Legit KillAura module", Category.COMBAT, false, false, false);
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (!timer.passedMs(1000 / cps.getValue())) return;

        Entity target = getTarget();

        if (target != null) {
            mc.playerController.attackEntity(mc.player, target);
            mc.player.swingArm(mc.player.getActiveHand());
            timer.reset();
        }
    }

    private Entity getTarget() {
        Entity closest = null;
        double closestDistance = range.getValue();

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity == mc.player || !entity.isEntityAlive()) continue;

            if (onlyPlayers.getValue() && !(entity instanceof EntityPlayer)) continue;

            double distance = mc.player.getDistance(entity);
            if (distance > closestDistance) continue;

            if (lineOfSight.getValue() && !mc.player.canEntityBeSeen(entity)) continue;

            closest = entity;
            closestDistance = distance;
        }

        return closest;
    }
}
