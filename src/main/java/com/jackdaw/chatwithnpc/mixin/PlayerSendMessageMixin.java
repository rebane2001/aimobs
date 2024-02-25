package com.jackdaw.chatwithnpc.mixin;

import com.jackdaw.chatwithnpc.event.PlayerSendMessageCallback;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerSendMessageMixin {
	@Inject(at = @At("HEAD"), method = "sendMessage(Lnet/minecraft/text/Text;)V", cancellable = true)
	private void onSend(Text message, CallbackInfo ci) {
		ActionResult result = PlayerSendMessageCallback.EVENT.invoker().interact((ServerPlayerEntity) (Object) this, message.getString());

		if(result == ActionResult.FAIL) {
			ci.cancel();
		}
	}
}