package com.jackdaw.chatwithnpc.mixin;

import com.jackdaw.chatwithnpc.ChatWithNPCMod;
import com.jackdaw.chatwithnpc.event.PlayerSendMessageCallback;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class PlayerSendMessageMixin {
	@Shadow public abstract ServerPlayerEntity getPlayer();

	@Inject(at = @At("HEAD"), method = "onChatMessage", cancellable = true)
	private void onSend(ChatMessageC2SPacket packet, CallbackInfo ci) {
		ActionResult result = PlayerSendMessageCallback.EVENT.invoker().interact(this.getPlayer(), packet.chatMessage());

		if(result == ActionResult.FAIL) {
			ci.cancel();
		}
	}
}