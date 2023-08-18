package com.rebane2001.aimobs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AIMobsMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("aimobs");
    // Initialize the ConversationsManager
    public static final ConversationsManager conversationsManager = new ConversationsManager();

    @Override
    public void onInitializeClient() {
        AIMobsConfig.loadConfig();
        // Load the conversations from the file
        conversationsManager.loadConversations();

        ClientCommandRegistrationCallback.EVENT.register(AIMobsCommand::setupAIMobsCommand);

        // Registering the UseEntityCallback to make the mob look at the player when shift-attacked
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!AIMobsConfig.config.enabled) return ActionResult.PASS;
            if (!player.isSneaking()) {
                if (entity.getUuid().equals(ActionHandler.entityId))
                    ActionHandler.handlePunch(entity, player);
                return ActionResult.PASS;
            }
            if (entity instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity) entity;

                // If the entity is a MobEntity or a VillagerEntity, make it walk to the player
                if (!world.isClient()) {
                    mobEntity.getNavigation().startMovingTo(player, 0.1D);
                }

                // Start the conversation
                if (world.isClient()) {
                    ActionHandler.startConversation(entity, player);
                }

                return ActionResult.SUCCESS; // Return success to indicate that the interaction was handled
            }
            return ActionResult.PASS; // Return pass to allow other interactions to proceed
        });

    }
}
