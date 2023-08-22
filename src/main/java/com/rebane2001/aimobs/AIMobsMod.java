package com.rebane2001.aimobs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents; // Added import
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;



public class AIMobsMod implements ClientModInitializer {
    public static final KeyBinding R_KEY_BINDING = new KeyBinding("key.aimobs.voice_input", GLFW.GLFW_KEY_R, "category.aimobs");
    public static final Logger LOGGER = LoggerFactory.getLogger("aimobs");
    public static final ConversationsManager conversationsManager = new ConversationsManager();

    // These fields need to be public or have public accessors to be used in the mixin
    public static MobEntity currentMob = null;
    public static PlayerEntity currentPlayer = null;

    @Override
    public void onInitializeClient() {
        AIMobsConfig.loadConfig();
        KeyBindingHelper.registerKeyBinding(R_KEY_BINDING);
        conversationsManager.loadConversations();
        MobPrompts.initializeMobStatsFile();

        ClientCommandRegistrationCallback.EVENT.register(AIMobsCommand::setupAIMobsCommand);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (AIMobsMod.R_KEY_BINDING.wasPressed()) {
                ActionHandler.onRKeyPress();
            }
            if (!AIMobsMod.R_KEY_BINDING.isPressed() && ActionHandler.isRKeyPressed) {
                ActionHandler.onRKeyRelease();
            }
        });

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!AIMobsConfig.config.enabled) return ActionResult.PASS;
            if (!player.isSneaking()) {
                if (entity.getUuid().equals(ActionHandler.entityId))
                    ActionHandler.handlePunch(entity, player);
                return ActionResult.PASS;
            }
            if (entity instanceof MobEntity) {
                currentMob = (MobEntity) entity;
                currentPlayer = player;

                if (world.isClient()) {
                    ActionHandler.startConversation(entity, player);
                }

                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (!AIMobsConfig.config.enabled) return ActionResult.PASS;
            if (entity instanceof MobEntity) {
                MobEntity mobEntity = (MobEntity) entity;
                System.out.println("Coming");
                for (int i = 0; i < 1000; i++) {
                    mobEntity.getNavigation().startMovingTo(player, 1.0); // Speed value, you can change it to whatever you want
                }
                // Rest of the code
                return ActionResult.SUCCESS; // Return success to indicate that the interaction was handled
            }
            return ActionResult.PASS; // Return pass to allow other interactions to proceed
        });


    }

    // This method will be called from the server-side mixin
    public static void updateMobMovement() {
        if (currentMob != null && currentPlayer != null && !ActionHandler.checkConversationEnd()) {
            double desiredDistance = 9.0; // Squared distance at which the mob should stop
                double distance = currentMob.squaredDistanceTo(currentPlayer);
                if (distance > desiredDistance) {
                    for (int i = 0; i < 1000; i++) {
                        currentMob.getNavigation().startMovingTo(currentPlayer, 1.0);
                    }
                } else {
                    currentMob.getNavigation().stop();
                }
        } else if (currentMob != null) {
            currentMob.getNavigation().stop();
            currentMob = null;
            currentPlayer = null;
        }
    }
}
