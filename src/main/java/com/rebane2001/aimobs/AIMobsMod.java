package com.rebane2001.aimobs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.util.math.Box;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;

import java.util.List;
import java.util.Random;

public class AIMobsMod implements ClientModInitializer {
    public static final KeyBinding R_KEY_BINDING = new KeyBinding("key.aimobs.voice_input", GLFW.GLFW_KEY_R, "category.aimobs");
    public static final Logger LOGGER = LoggerFactory.getLogger("aimobs");
    public static final ConversationsManager conversationsManager = new ConversationsManager();

    // These fields need to be public or have public accessors to be used in the mixin
    public static MobEntity current_mob = null;
    public static PlayerEntity current_player = null;
    public static long nextMobSelectionTime = 0;

    @Override
    public void onInitializeClient() {
        AIMobsConfig.loadConfig();
        KeyBindingHelper.registerKeyBinding(R_KEY_BINDING);
        conversationsManager.loadConversations();
        MobPrompts.initializeMobStatsFile();

        ClientCommandRegistrationCallback.EVENT.register(AIMobsCommand::setupAIMobsCommand);

        // Tick event
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!AIMobsConfig.config.enabled) return;
            //mobInitiative(client.player);
        });



        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!AIMobsConfig.config.enabled) return;
            //current_player = MinecraftClient.getInstance().player;
            //ActionHandler.mobInitiative(current_player);
            if (ActionHandler.checkConversationEnd()) {
                current_player.sendMessage(Text.of("Bye."));
            }
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
                current_mob = (MobEntity) entity;
                current_player = player;

                if (world.isClient()) {
                    //current_player.sendMessage(Text.of(current_mob + " coming to "+ current_player));
                    ActionHandler.startConversation(entity, player);
                }

                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });

    /*    UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
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
        }); */


    }

    /*     // This method will be called from the server-side mixin
    public static void updateMobMovement() {
        MobEntity currentMob = ActionHandler.currentMob; // Get currentMob from ActionHandler
        if (currentMob != null && currentPlayer != null && ActionHandler.conversationOngoing) {
            double desiredDistance = 9.0; // Squared distance at which the mob should stop
                double distance = currentMob.squaredDistanceTo(currentPlayer);
                if (distance > desiredDistance) {
                    currentMob.getNavigation().startMovingTo(currentPlayer, 1.0);
                } else {
                    currentMob.getNavigation().stop();
                }
        } else if (currentMob != null) {
            currentMob.getNavigation().stop();
            //currentMob = null;
            //currentPlayer = null;
        }
    } */

    // This method will be called from the server-side mixin
    public static void updateMobMovement() {
        if (current_mob != null && current_player != null && ActionHandler.conversationOngoing) {
            double desiredDistance = 9.0; // Squared distance at which the mob should stop
                double distance = current_mob.squaredDistanceTo(current_player);
                if (distance > desiredDistance) {
                    for (int i = 0; i < 1000; i++) {
                        current_mob.getNavigation().startMovingTo(current_player, 1.0);
                    }
                } else {
                    current_mob.getNavigation().stop();
                }
        } else if (current_mob != null) {
            current_mob.getNavigation().stop();
            //current_mob = null;
            //current_player = null;
        }
    }

        public static void mobInitiative(PlayerEntity player) {
        if (player == null || ActionHandler.conversationOngoing) {
            return;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime < nextMobSelectionTime) {
            return;
        }
        // Create a bounding box around the player
        Box boundingBox = new Box(player.getBlockPos()).expand(10); // 10-block radius
        // Get the nearby entities, excluding the player
        List<Entity> nearbyEntities = player.world.getEntitiesByClass(Entity.class, boundingBox, e -> e != player);
        if (nearbyEntities.isEmpty()) {
            return;
        }
        Random random = new Random();
        current_mob = (MobEntity) nearbyEntities.get(random.nextInt(nearbyEntities.size()));
        System.out.println("Selected mob: " + current_mob.getName().getString()); // Test message
        // Start the conversation with the selected mob
        ActionHandler.startConversation(current_mob, player);
        // Set the next mob selection time with a random cooldown between 1-5 minutes
        nextMobSelectionTime = currentTime + (1 + random.nextInt(5)) * 60 * 1000;
    }

}

