package me.alpha432.oyvey.features.modules.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashSet;
import java.util.Set;

public class Xray {

    private final MinecraftClient mc = MinecraftClient.getInstance();
    private boolean enabled = false;

    // Set of blocks that Xray will show (e.g., ores, chests, etc.)
    private final Set<Block> xrayBlocks = new HashSet<>();

    public Xray() {
        // Add blocks you want to see with Xray
        xrayBlocks.add(Blocks.DIAMOND_ORE);
        xrayBlocks.add(Blocks.GOLD_ORE);
        xrayBlocks.add(Blocks.IRON_ORE);
        xrayBlocks.add(Blocks.COAL_ORE);
        xrayBlocks.add(Blocks.EMERALD_ORE);
        xrayBlocks.add(Blocks.LAPIS_ORE);
        xrayBlocks.add(Blocks.REDSTONE_ORE);
        xrayBlocks.add(Blocks.NETHER_QUARTZ_ORE);
        xrayBlocks.add(Blocks.CHEST);
        // Add more if desired
    }

    public void enable() {
        if (enabled) return;

        // Register a render event listener to filter blocks
        WorldRenderEvents.BLOCK_OUTLINE.register(this::onBlockOutline);
        WorldRenderEvents.BLOCK_RENDER.register(this::onBlockRender);

        enabled = true;
    }

    public void disable() {
        if (!enabled) return;

        // Unregister the listeners if you have a way, or restart the client (Fabric API might not provide direct unregister)
        // This is a limitation here; better use a toggle with checks inside handlers.

        enabled = false;
    }

    // This event can be used to cancel rendering blocks that are not in xrayBlocks
    private boolean onBlockRender(WorldRenderEvents.BlockRenderContext context) {
        Block block = mc.world.getBlockState(context.blockPos()).getBlock();
        return xrayBlocks.contains(block);
    }

    // This event cancels outline rendering on non-xray blocks (optional)
    private boolean onBlockOutline(WorldRenderEvents.BlockOutlineContext context) {
        Block block = mc.world.getBlockState(context.blockPos()).getBlock();
        return xrayBlocks.contains(block);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
