package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.models.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AutoAttack extends Module {
    // Settings
    private final Setting<Float> range = register(new Setting<>("Range", 3.0f, 0.1f, 10.0f));
    private final Setting<Integer> delay = register(new Setting<>("Delay", 500, 0, 1000));
    private final Setting<Boolean> playersOnly = register(new Setting<>("PlayersOnly", true));
    private final Setting<Boolean> rotate = register(new Setting<>("Rotate", true));
    private final Setting<Boolean> silentRotate = register(new Setting<>("SilentRotate", false));
    
    private final Timer attackTimer = new Timer();
    private Entity target;

    public AutoAttack() {
        super("AutoAttack", "Automatically attacks players", Category.COMBAT, true, false, false);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        target = findBestTarget();
        
        if (target != null && attackTimer.passedMs(delay.getValue())) {
            if (rotate.getValue()) faceTarget(target, silentRotate.getValue());
            attack(target);
            attackTimer.reset();
        }
    }

    private Entity findBestTarget() {
        List<Entity> validEntities = new ArrayList<>();
        
        // Manual iteration instead of streams
        for (Entity entity : mc.world.getEntities()) {
            if (isValidEntity(entity)) {
                validEntities.add(entity);
            }
        }
        
        // Find closest
        Optional<Entity> closest = validEntities.stream()
            .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)));
            
        return closest.orElse(null);
    }

    private boolean isValidEntity(Entity entity) {
        if (entity == mc.player) return false;
        if (mc.player.distanceTo(entity) > range.getValue()) return false;
        if (playersOnly.getValue() && !(entity instanceof PlayerEntity)) return false;
        return true;
    }

    private void faceTarget(Entity target, boolean silent) {
        // ... (keep your existing rotation logic)
    }

    private void attack(Entity target) {
        if (mc.interactionManager == null) return;
        mc.interactionManager.attackEntity(mc.player, target);
        mc.player.swingHand(mc.player.getActiveHand());
    }
}
