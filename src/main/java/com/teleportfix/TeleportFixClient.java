package com.teleportfix;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TeleportFix 0.1.0 — safe first-stage client diagnostics.
 * It measures client tick stalls around server teleports instead of
 * applying risky changes to chunk/network internals.
 */
public final class TeleportFixClient implements ClientModInitializer {
    public static final String MOD_ID = "teleportfix";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static volatile long teleportDetectedAt = -1L;
    private static long lastTickNanos = 0L;
    private static int worstTickMs = 0;
    private static int slowTicks = 0;
    private static boolean monitoring = false;

    @Override
    public void onInitializeClient() {
        LOGGER.info("TeleportFix 0.1.0 initialized for Minecraft 1.21.11");
        ClientTickEvents.END_CLIENT_TICK.register(TeleportFixClient::onClientTick);
    }

    /** Called by the network-handler mixin after the teleport packet is handled. */
    public static void onServerTeleport() {
        teleportDetectedAt = System.currentTimeMillis();
        worstTickMs = 0;
        slowTicks = 0;
        monitoring = true;
        lastTickNanos = System.nanoTime();
        LOGGER.info("Server teleport detected; monitoring client performance for 8 seconds.");
    }

    private static void onClientTick(MinecraftClient client) {
        long nowNanos = System.nanoTime();
        if (lastTickNanos != 0L && monitoring) {
            long elapsedMs = (nowNanos - lastTickNanos) / 1_000_000L;
            if (elapsedMs > worstTickMs) worstTickMs = (int) Math.min(elapsedMs, Integer.MAX_VALUE);
            if (elapsedMs >= 100L) slowTicks++;
        }
        lastTickNanos = nowNanos;

        if (!monitoring || teleportDetectedAt < 0L) return;
        long elapsed = System.currentTimeMillis() - teleportDetectedAt;
        if (elapsed >= 8_000L) {
            monitoring = false;
            String diagnosis = worstTickMs >= 1000
                    ? "Major client-side stall detected"
                    : worstTickMs >= 250
                    ? "Noticeable client-side stall detected"
                    : "No major client tick stall measured";
            LOGGER.info("TeleportFix report: {}. Worst tick: {} ms; slow ticks (>=100 ms): {}.",
                    diagnosis, worstTickMs, slowTicks);
            if (client.player != null) {
                client.player.sendMessage(Text.literal("[TeleportFix] " + diagnosis
                        + " | worst tick: " + worstTickMs + " ms"), false);
            }
        }
    }
}
