package com.jackdaw.chatwithnpc;

import com.jackdaw.chatwithnpc.auxiliary.command.CommandSet;
import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import com.jackdaw.chatwithnpc.environment.EnvironmentManager;
import com.jackdaw.chatwithnpc.event.ConversationHandler;
import com.jackdaw.chatwithnpc.event.ConversationManager;
import com.jackdaw.chatwithnpc.npc.LivingNPCEntity;
import com.jackdaw.chatwithnpc.npc.NPCEntityManager;
import com.jackdaw.chatwithnpc.npc.VillagerNPCEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChatWithNPCMod implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("chat-with-npc");

    public static final Path workingDirectory = Paths.get(System.getProperty("user.dir"), "config", "chat-with-npc");

    // The time in milliseconds that a static data is considered out of time
    public static final long outOfTime = 300000L;

    // The time in milliseconds that check for out of time static data
    public static final long updateInterval = 1000L;

    @Override
    public void onInitializeClient() {
        if (!Files.exists(workingDirectory)) {
            try {
                Files.createDirectories(workingDirectory);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
        SettingManager.loadConfig();
        // Load the global environment, and it will not be removed until the game is closed
        EnvironmentManager.loadEnvironment("Global");
        ClientCommandRegistrationCallback.EVENT.register(CommandSet::setupCommand);
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!SettingManager.enabled) return ActionResult.PASS;
            if (!player.isSneaking()) return ActionResult.PASS;
            // The entity must have a custom name to be an NPC
            if (entity.getCustomName() == null) return ActionResult.PASS;
            String name = entity.getCustomName().getString();
            // register the NPC entity and start a conversation
            if (entity instanceof VillagerEntity villager) {
                NPCEntityManager.registerNPCEntity(name, new VillagerNPCEntity(villager));
                ConversationManager.startConversation(NPCEntityManager.getNPCEntity(name), player);
            } else if (entity instanceof LivingEntity entityLiving) {
                NPCEntityManager.registerNPCEntity(name, new LivingNPCEntity(entityLiving));
                ConversationManager.startConversation(NPCEntityManager.getNPCEntity(name), player);
            } else {
                return ActionResult.PASS;
            }
            return ActionResult.FAIL;
        });
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
