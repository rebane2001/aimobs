package com.jackdaw.chatwithnpc;

import com.jackdaw.chatwithnpc.auxiliary.command.CommandSet;
import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import com.jackdaw.chatwithnpc.environment.EnvironmentManager;
import com.jackdaw.chatwithnpc.conversation.ConversationManager;
import com.jackdaw.chatwithnpc.npc.NPCEntityManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChatWithNPCMod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("chat-with-npc");

    public static final Path workingDirectory = Paths.get(System.getProperty("user.dir"), "config", "chat-with-npc");

    // The time in milliseconds that a static data is considered out of time
    public static final long outOfTime = 300000L;

    // The time in milliseconds that check for out of time static data
    public static final long updateInterval = 30000L;

    @Override
    public void onInitialize() {
        // Create the working directory if it does not exist
        if (!Files.exists(workingDirectory)) {
            try {
                Files.createDirectories(workingDirectory);
            } catch (IOException e) {
                LOGGER.error("Failed to create the working directory");
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        // Load the configuration
        SettingManager.loadConfig();
        // Load the global environment, and it will not be removed until the game is closed
        EnvironmentManager.loadEnvironment("Global");
        // Register the command
        CommandRegistrationCallback.EVENT.register(CommandSet::setupCommand);
        // Register the conversation
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            // The mod must be enabled
            if (!SettingManager.enabled) return ActionResult.PASS;
            // The player must be sneaking to start a conversation
            if (!player.isSneaking()) return ActionResult.PASS;
            // The entity must have a custom name to be an NPC
            if (entity.getCustomName() == null) return ActionResult.PASS;
            String name = entity.getCustomName().getString();
            // register the NPC entity and start a conversation
            NPCEntityManager.registerNPCEntity(name, entity);
            ConversationManager.startConversation(NPCEntityManager.getNPCEntity(name), player);
            return ActionResult.FAIL;
        });
        // Register the player chat event

        LOGGER.info("ChatWithNPCMod has been Loaded");
        // Check for out of time static data
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(updateInterval);
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage());
                }
                UpdateStaticData.update();
            }
        }).start();
    }
}
