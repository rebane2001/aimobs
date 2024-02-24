package com.jackdaw.chatwithnpc;

import com.jackdaw.chatwithnpc.auxiliary.command.CommandSet;
import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import com.jackdaw.chatwithnpc.event.EventHandler;
import com.jackdaw.chatwithnpc.npc.ActionHandler;
import com.jackdaw.chatwithnpc.npc.LivingNPCEntity;
import com.jackdaw.chatwithnpc.npc.VillagerNPCEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.Entity;
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
        ClientCommandRegistrationCallback.EVENT.register(CommandSet::setupCommand);
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!SettingManager.enabled) return ActionResult.PASS;
            if (!player.isSneaking()) {return ActionResult.PASS;}
            // ActionHandler.startConversation(entity, player);
            EventHandler eventHandler;
            if (entity instanceof VillagerEntity villager) {
                eventHandler = new EventHandler(new VillagerNPCEntity(villager), player);
            } else if (entity instanceof LivingEntity entityLiving) {
                eventHandler = new EventHandler(new LivingNPCEntity(entityLiving), player);
            } else {
                return ActionResult.PASS;
            }
            eventHandler.startConversation();
            return ActionResult.FAIL;
        });
    }
}
