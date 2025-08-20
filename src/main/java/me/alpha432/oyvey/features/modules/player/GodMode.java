package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;

import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketType;

import java.util.concurrent.atomic.AtomicBoolean;

public class GodMode extends Module {

    private static final PacketType.Id<EntityStatusS2CPacket> ENTITY_STATUS = PacketType.id("minecraft:entity_status");
    private static final PacketType.Id<EntityVelocityUpdateS2CPacket> ENTITY_VELOCITY = PacketType.id("minecraft:entity_velocity");
    private static final PacketType.Id<EntityTrackerUpdateS2CPacket> ENTITY_METADATA = PacketType.id("minecraft:entity_tracker_update");

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private Thread spoofThread;

    public GodMode() {
        super("GodMode", "Client desync to fake invulnerability on servers", Module.Category.PLAYER, false, false, false);
    }

    @Override
    public void onEnable() {
        enabled.set(true);

        registerPacketInterceptors();

        spoofThread = new Thread(() -> {
            while (enabled.get()) {
                sendSpoofedPosition();
                try {
                    Thread.sleep(50);
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
            if (mc.player != null && status == 2) {
                client.execute(() -> mc.player.hurtTime = 0);
            }
        });

        ClientPlayNetworking.registerReceiver(ENTITY_VELOCITY, (client, handler, buf, sender) -> {
            int entityId = buf.readInt();
            if (mc.player != null && entityId == mc.player.getId()) {
                // Cancel knockback by ignoring velocity packets
            }
        });

        ClientPlayNetworking.registerReceiver(ENTITY_METADATA, (client, handler, buf, sender) -> {
            int entityId = buf.readInt();
            if (mc.player != null && entityId == mc.player.getId()) {
                // Potentially skip metadata updates to fake god mode
            }
        });
    }

    private void sendSpoofedPosition() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        double x = mc.player.getX();
        double y = mc.player.getY() + 1.5; // Slightly above to desync position
        double z = mc.player.getZ();
        boolean onGround = mc.player.isOnGround();

        PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.PositionAndOnGround(new Vec3d(x, y, z), onGround, false);
        mc.getNetworkHandler().sendPacket(packet);
    }
}
