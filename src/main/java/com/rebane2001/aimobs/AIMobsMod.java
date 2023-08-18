package com.rebane2001.aimobs;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
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

        /* AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			player.sendMessage(Text.of("The initial has been tested."));
            if (!AIMobsConfig.config.enabled) return ActionResult.PASS;
            if (!player.isSneaking()) {
                if (entity.getId() == ActionHandler.entityId)
                    ActionHandler.handlePunch(entity, player);
                return ActionResult.PASS;
            }
            ActionHandler.startConversation(entity, player);
            return ActionResult.FAIL;
        }); */

        // Registering the UseEntityCallback to toggle NoAI status
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
		if (!AIMobsConfig.config.enabled || !world.isClient()) return ActionResult.PASS;
		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity;
			NbtCompound nbt = livingEntity.writeNbt(new NbtCompound());
			boolean isFrozen = nbt.getBoolean("NoAI");

			// Toggle the NoAI status
			nbt.putBoolean("NoAI", !isFrozen);
			livingEntity.readNbt(nbt);

			// Send a message to the player about the change
			if (!isFrozen) {
				player.sendMessage(Text.of("The " + livingEntity.getName().getString() + " has been frozen."), false);
				ActionHandler.startConversation(entity, player);
			} else {
				player.sendMessage(Text.of("The " + livingEntity.getName().getString() + " has been unfrozen."), false);
			}

			return ActionResult.SUCCESS; // Return success to indicate that the interaction was handled
		}
		return ActionResult.PASS; // Return pass to allow other interactions to proceed
	});


    }
}
