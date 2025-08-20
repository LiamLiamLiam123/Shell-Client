package me.alpha432.oyvey.features.modules.player;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityMetadataS2CPacket;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.PlayChannelHandler;
import net.minecraft.util.Identifier;
import net.minecraft.network.PacketByteBuf;

public class GodModeClient implements ClientModInitializer {
    private static final Identifier ENTITY_STATUS_ID = new Identifier("minecraft", "entity_status");
    private static final Identifier ENTITY_VELOCITY_ID = new Identifier("minecraft", "entity_velocity");
    private static final Identifier ENTITY_METADATA_ID = new Identifier("minecraft", "entity_metadata");

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            intercept(ENTITY_STATUS_ID, this::onEntityStatus);
            intercept(ENTITY_VELOCITY_ID, this::onEntityVelocity);
            intercept(ENTITY_METADATA_ID, this::onEntityMetadata);
        });
    }

    private void intercept(Identifier id, PlayChannelHandler handler) {
        ClientPlayNetworking.registerReceiver(id, handler);
    }

    private void onEntityStatus(MinecraftClient client, var ctx, PacketByteBuf buf, var sender) {
        byte status = buf.readByte();
        if (client.player != null && status == 2) { // 2 = HURT
            client.execute(() -> client.player.hurtTime = 0);
            return; // Cancel packet
        }
        ctx.onPacket(new EntityStatusS2CPacket(client.player, status));
    }

    private void onEntityVelocity(MinecraftClient client, var ctx, PacketByteBuf buf, var sender) {
        int entityId = buf.readInt();
        double x = buf.readDouble(), y = buf.readDouble(), z = buf.readDouble();
        if (client.player != null && client.player.getId() == entityId) {
            return; // cancel knockback
        }
        ctx.onPacket(new EntityVelocityUpdateS2CPacket(entityId, x, y, z));
    }

    private void onEntityMetadata(MinecraftClient client, var ctx, PacketByteBuf buf, var sender) {
        int id = buf.readInt();
        if (client.player != null && client.player.getId() == id) {
            buf.readCollection(list -> null); // skip metadata
            return;
        }
        // Reconstruct metadata packet with read values, if needed:
        ctx.onPacket(new EntityMetadataS2CPacket(id, buf.readList(it -> it.readPacket(buf))));
    }

    // Example spoofing via movement packets:
    public void sendSpoofMove(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        PlayerMoveC2SPacket packet = new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
    }
}
