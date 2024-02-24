package com.jackdaw.chatwithnpc;

import com.jackdaw.chatwithnpc.auxiliary.command.AIMobsCommand;
import com.jackdaw.chatwithnpc.auxiliary.configuration.AIMobsConfig;
import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import com.jackdaw.chatwithnpc.npc.ActionHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ChatWithNPCMod implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("chat-with-npc");

    @Override
    public void onInitializeClient() {
        String runDir = System.getProperty("user.dir");
        Path configPath = Paths.get(runDir, "config", "chat-with-npc");
        if (!Files.exists(configPath)) {
            try {
                Files.createDirectories(configPath);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        SettingManager setting = getSettingManager(configPath);
        if (setting == null) {
            LOGGER.error("Can't load config file.");
            return;
        }

        AIMobsConfig.loadConfig();
        ClientCommandRegistrationCallback.EVENT.register(AIMobsCommand::setupAIMobsCommand);
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!AIMobsConfig.config.enabled) return ActionResult.PASS;
            if (!player.isSneaking()) {
                if (entity.getId() == ActionHandler.entityId)
                    ActionHandler.handlePunch(entity, player);
                return ActionResult.PASS;
            }
            ActionHandler.startConversation(entity, player);
            return ActionResult.FAIL;
        });
    }

    @Nullable
    private SettingManager getSettingManager(@NotNull Path dataDirectory) {
        SettingManager setting;
        try {
            setting = new SettingManager(dataDirectory.toFile(), LOGGER);
        } catch (IOException ioException) {
            System.out.println(ioException.getMessage());
            return null;
        }
        return setting;
    }
}
