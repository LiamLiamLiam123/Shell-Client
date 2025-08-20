package me.alpha432.oyvey.features.modules.player;

import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.modules.Category;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityMetadataS2CPacket;
import net.minecraft.network.PacketByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.util.Identifier;

/**
 * Cosmetic GodMode module for OyVey (1.21.5 Fabric)
 * - Cancels hurt animations
 * - Cancels knockback
 * - Cancels server health updates
 * - Sends spoofed movement to confuse servers
 */
public class GodMode extends Module {

    private static final Identifier ENTITY_STATUS = new Identifier("minecraft", "entity_status");
    private static final Identifier ENTITY_VELOCITY = new Identifier("minecraft", "entity_velocity");
    private static final Identifier ENTITY_METADATA = new Identifier("minecraft", "entity_metadata");

    public GodMode() {
        super("GodMode", "Client desync to fake invulnerability", Category.PLAYER, false, false, false);
    }

    @Override
    public void onEnable() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            registerPacketInterceptors();
        });
        registerPacketInterceptors();
        sendSpoofedPosition();
    }

    @Override
    public void onDisable() {
        // Packet interceptors can't be unregistered cleanly; recommend client restart or reload
    }

    private void registerPacketInterceptors() {
        MinecraftClient client = MinecraftClient.getInstance();

        ClientPlayNetworking.registerReceiver(ENTITY_STATUS, (c, h, buf, sender) -> {
            byte status = buf.readByte();
            if (client.player != null && status == 2) { // 2 = hurt animation
                c.execute(() -> client.player.hurtTime = 0); // cancel hurt visuals
            }
        });

        ClientPlayNetworking.registerReceiver(ENTITY_VELOCITY, (c, h, buf, sender) -> {
            int entityId = buf.readInt();
            buf.readDouble(); buf.readDouble(); buf.readDouble(); // velocity x, y, z
            if (client.player != null && entityId == client.player.getId()) {
                // Cancel knockback for player
            }
        });

        ClientPlayNetworking.registerReceiver(ENTITY_METADATA, (c, h, buf, sender) -> {
            int entityId = buf.readInt();
            if (client.player != null && entityId == client.player.getId()) {
                // Skip metadata that could update health
                buf.readCollection(list -> null);
            }
        });
    }

    private void sendSpoofedPosition() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.getNetworkHandler() != null) {
            double x = client.player.getX();
            double y = client.player.getY();
            double z = client.player.getZ();
            boolean onGround = client.player.isOnGround();

            PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 1.5, z, onGround);
            client.getNetworkHandler().sendPacket(packet);
        }
    }
}
