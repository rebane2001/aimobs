package com.jackdaw.chatwithnpc.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;

/**
 * Callback for player sending message.
 * Is hooked in before the spectator check, so make sure to check for the player's game mode as well!
 *
 * <p>Upon return:
 * <ul><li>SUCCESS cancels further processing and, on the client, sends a packet to the server.
 * <li>PASS falls back to further processing.
 * <li>FAIL cancels further processing and does not send a packet to the server.</ul>
 */
public interface PlayerSendMessageCallback {
    Event<PlayerSendMessageCallback> EVENT = EventFactory.createArrayBacked(PlayerSendMessageCallback.class,
            (listeners) -> (player, message) -> {
                for (PlayerSendMessageCallback listener : listeners) {
                    ActionResult result = listener.interact(player, message);

                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }

                return ActionResult.PASS;
            });

    ActionResult interact(PlayerEntity player, String message);
}
