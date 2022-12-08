package com.rebane2001.aimobs.mixin;

import com.rebane2001.aimobs.AIMobsConfig;
import com.rebane2001.aimobs.ActionHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.network.ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandler {
	@Inject(method = "sendChatMessage", at = @At("HEAD"))
	public void sendChatMessage(String message, CallbackInfo ci) {
		if (!AIMobsConfig.config.enabled) return;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;
		ActionHandler.replyToEntity(message, player);
	}
}
