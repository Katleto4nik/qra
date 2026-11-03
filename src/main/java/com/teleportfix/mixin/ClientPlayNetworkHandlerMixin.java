package com.teleportfix.mixin;

import com.teleportfix.TeleportFixClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Detects server-authoritative position corrections/teleports. */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onPlayerPositionLook", at = @At("TAIL"))
    private void teleportfix$afterServerTeleport(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        TeleportFixClient.onServerTeleport();
    }
}
