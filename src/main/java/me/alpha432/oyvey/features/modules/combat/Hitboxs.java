package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Hitboxs extends Module {
    public Setting<Double> extendSize = this.register(new Setting<>("ExtendSize", 0.5, 0.1, 2.0));
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public Hitboxs() {
        super("Hitboxs", "Expands visible hitboxes of living entities.", Category.COMBAT, true, false, false);
    }

    public void onRender3D(MatrixStack matrices, VertexConsumerProvider provider) {
        if (mc.world == null || mc.player == null) return;

        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();
        double expand = extendSize.getValue();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity) || entity == mc.player || !entity.isAlive()) continue;

            Box box = entity.getBoundingBox().expand(expand, 0, expand);
            Box renderBox = box.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            drawBoxOutline(matrices, provider, renderBox, 1f, 0f, 0f, 1f);
        }
    }

    private void drawBoxOutline(MatrixStack matrices, VertexConsumerProvider provider, Box box, float r, float g, float b, float alpha) {
        VertexConsumer consumer = provider.getBuffer(RenderLayer.getLines());
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float x1 = (float) box.minX;
        float y1 = (float) box.minY;
        float z1 = (float) box.minZ;
        float x2 = (float) box.maxX;
        float y2 = (float) box.maxY;
        float z2 = (float) box.maxZ;

        // Vertical edges
        drawLine(consumer, matrix, x1, y1, z1, x1, y2, z1, r, g, b, alpha);
        drawLine(consumer, matrix, x2, y1, z1, x2, y2, z1, r, g, b, alpha);
        drawLine(consumer, matrix, x1, y1, z2, x1, y2, z2, r, g, b, alpha);
        drawLine(consumer, matrix, x2, y1, z2, x2, y2, z2, r, g, b, alpha);

        // Bottom rectangle
        drawLine(consumer, matrix, x1, y1, z1, x2, y1, z1, r, g, b, alpha);
        drawLine(consumer, matrix, x2, y1, z1, x2, y1, z2, r, g, b, alpha);
        drawLine(consumer, matrix, x2, y1, z2, x1, y1, z2, r, g, b, alpha);
        drawLine(consumer, matrix, x1, y1, z2, x1, y1, z1, r, g, b, alpha);

        // Top rectangle
        drawLine(consumer, matrix, x1, y2, z1, x2, y2, z1, r, g, b, alpha);
        drawLine(consumer, matrix, x2, y2, z1, x2, y2, z2, r, g, b, alpha);
        drawLine(consumer, matrix, x2, y2, z2, x1, y2, z2, r, g, b, alpha);
        drawLine(consumer, matrix, x1, y2, z2, x1, y2, z1, r, g, b, alpha);
    }

    private void drawLine(VertexConsumer consumer, Matrix4f matrix, float x1, float y1, float z1,
                          float x2, float y2, float z2, float r, float g, float b, float alpha) {
        consumer.vertex(matrix, x1, y1, z1).color(r, g, b, alpha).next();
        consumer.vertex(matrix, x2, y2, z2).color(r, g, b, alpha).next();
    }
}


