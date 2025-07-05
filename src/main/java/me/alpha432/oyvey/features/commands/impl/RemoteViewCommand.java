package me.alpha432.oyvey.features.commands.impl;

import me.alpha432.oyvey.features.commands.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

public class RemoteViewCommand extends Command {

    private EntityOtherPlayerMP fakeCamEntity = null;
    private final MinecraftClient client = MinecraftClient.getInstance();

    public RemoteViewCommand() {
        super("rv", new String[]{"<player>", "stop"});
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            sendMessage("Usage: .rv <player> or .rv stop");
            return;
        }

        if (args[0].equalsIgnoreCase("stop")) {
            stopRemoteView();
            return;
        }

        String targetName = args[0];
        PlayerEntity target = client.world.getPlayerByName(targetName);

        if (target == null) {
            sendMessage("Player not found: " + targetName);
            return;
        }

        startRemoteView(target);
    }

    private void startRemoteView(PlayerEntity target) {
        if (fakeCamEntity != null) {
            stopRemoteView();
        }

        fakeCamEntity = new EntityOtherPlayerMP(client.world, target.getGameProfile());
        fakeCamEntity.copyPositionAndRotation(target);
        fakeCamEntity.rotationYawHead = target.rotationYawHead;

        client.world.addEntity(-1337, fakeCamEntity);
        client.setCameraEntity(fakeCamEntity);

        loadChunksAround(target);
        sendMessage("Now remote viewing: " + target.getName().getString());
    }

    private void stopRemoteView() {
        if (fakeCamEntity != null) {
            client.setCameraEntity(client.player);
            client.world.removeEntity(fakeCamEntity);
            fakeCamEntity = null;
            sendMessage("Remote view stopped.");
        } else {
            sendMessage("You're not currently remote viewing anyone.");
        }
    }

    private void loadChunksAround(PlayerEntity target) {
        int chunkRadius = 3; // Adjust as needed
        int chunkX = (int) target.getX() >> 4;
        int chunkZ = (int) target.getZ() >> 4;

        for (int x = -chunkRadius; x <= chunkRadius; x++) {
            for (int z = -chunkRadius; z <= chunkRadius; z++) {
                Chunk chunk = client.world.getChunk(x + chunkX, z + chunkZ, ChunkStatus.FULL, false);
                if (chunk != null) {
                    chunk.setForced(true);
                }
            }
        }
    }

    @Override
    public void onTick() {
        if (fakeCamEntity != null && client.world != null) {
            PlayerEntity target = client.world.getPlayerByName(fakeCamEntity.getName());
            if (target != null && target.isAlive()) {
                fakeCamEntity.copyPositionAndRotation(target);
                fakeCamEntity.rotationYawHead = target.rotationYawHead;
                loadChunksAround(target);
            } else {
                stopRemoteView();
            }
        }
    }
}
