package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.Category;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityMetadataS2CPacket;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import net.minecraft.entity.player.PlayerEntity;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * GodMode Module — hides hurt animations, cancels knockback, 
 * repeatedly sends spoofed move packets to confuse simple servers.
 */
public class GodMode extends Module {

    private static final Identifier ENTITY_STATUS = new Identifier("minecraft", "entity_status");
    private static final Identifier ENTITY_VELOCITY = new Identifier("minecraft", "entity_velocity");
    private static final Identifier ENTITY_METADATA = new Identifier("minecraft", "entity_metadata");

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private Thread spoofThread;

    public GodMode() {
        super("GodMode", "Client desync to fake invulnerability on servers", Category.PLAYER, false, false, false);
    }

    @Override
    public void onEnable() {
        enabled.set(true);

        registerPacketInterceptors();

        // Start a thread to spam spoofed movement packets every 50ms
        spoofThread = new Thread(() -> {
            while (enabled.get()) {
                sendSpoofedPosition();
                try {
                    Thread.sleep(50); // 20 packets per second approx
                } catch (InterruptedException e) {
                    break;
                }
            }
        }, "GodModeSpoofThread");
        spoofThread.setDaemon(true);
        spoofThread.start();
    }

    @Override
    public void onDisable() {
        enabled.set(false);
        if (spoofThread != null && spoofThread.isAlive()) {
            spoofThread.interrupt();
        }
    }

    private void registerPacketInterceptors() {
        ClientPlayNetworking.registerReceiver(ENTITY_STATUS, (client, handler, buf, sender) -> {
            byte status = buf.readByte();
            if (mc.player != null && status == 2) { // 2 = hurt animation
                client.execute(() -> mc.player.hurtTime = 0);
                return; // Cancel hurt animation packet
            }
        });

        ClientPlayNetworking.registerReceiver(ENTITY_VELOCITY, (client, handler, buf, sender) -> {
            int entityId = buf.readInt();
            // Read velocities but don't apply if it's us
            if (mc.player != null && entityId == mc.player.getId()) {
                // skip velocity packet - cancel knockback
                return;
            }
            // Otherwise pass the packet normally (not canceling for others)
        });

        ClientPlayNetworking.registerReceiver(ENTITY_METADATA, (client, handler, buf, sender) -> {
            int entityId = buf.readInt();
            if (mc.player != null && entityId == mc.player.getId()) {
                // Skip metadata that could update health
                buf.readCollection(list -> null);
                return;
            }
        });
    }

    private void sendSpoofedPosition() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        double x = mc.player.getX();
        double y = mc.player.getY() + 1.5; // slightly raise Y to confuse server position checks
        double z = mc.player.getZ();
        boolean onGround = mc.player.isOnGround();

        PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround);
        mc.getNetworkHandler().sendPacket(packet);
    }
}
