package com.jackdaw.chatwithnpc.mixin;

import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import com.jackdaw.chatwithnpc.event.ConversationManager;
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
		if (!SettingManager.enabled) return;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player == null) return;
		ConversationManager.getConversation(player).replyToEntity(message);
	}
}
