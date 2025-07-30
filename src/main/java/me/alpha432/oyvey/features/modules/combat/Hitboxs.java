package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.MinecraftClient;

public class HitboxExtender extends Module {
    public Setting<Double> extendSize = this.register(new Setting<>("ExtendSize", 0.5, 0.1, 2.0));

    public HitboxExtender() {
        super("Hitboxs", "Expands the Player Hitbox in Fighting.", Category.COMBAT, true, false, false);
    }

    @Override
    public void onRender3D(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers) {
        MinecraftClient mc = MinecraftClient.getInstance();

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity && entity != mc.player) {
                Box hitbox = entity.getBoundingBox();
                double size = extendSize.getValue();
                Box extended = new Box(
                    hitbox.minX - size,
                    hitbox.minY,
                    hitbox.minZ - size,
                    hitbox.maxX + size,
                    hitbox.maxY + size,
                    hitbox.maxZ + size
                );
                WorldRenderer.drawBox(matrixStack, vertexConsumers.getBuffer(WorldRenderer.getOutlineLayer()), extended, 1f, 0f, 0f, 0.3f);
            }
        }
    }
}
