package me.alpha432.oyvey.features.modules.combat;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.settings.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class Hitboxs extends Module {
    public Setting<Double> extendSize = this.register(new Setting<>("ExtendSize", 0.5, 0.1, 2.0));

    public Hitboxs() {
        super("Hitboxs", "Expands the Player Hitbox in Fighting.", Category.COMBAT, true, false, false);
    }

    @Override
    public void onRender3D(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (mc.world == null || mc.player == null) return;

        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity) || entity == mc.player || !entity.isAlive()) continue;

            Box box = entity.getBoundingBox();
            double expand = extendSize.getValue();

            Box expanded = box.expand(expand, 0, expand);

            Box renderBox = expanded.offset(-cameraPos.x, -cameraPos.y, -cameraPos.z);

            // Render box
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(2.0F);

            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            WorldRenderer.drawBox(matrices, buffer, renderBox, 1f, 0f, 0f, 0.5f);
            Tessellator.getInstance().draw();

            RenderSystem.enableTexture();
            RenderSystem.disableBlend();
        }
    }
}
