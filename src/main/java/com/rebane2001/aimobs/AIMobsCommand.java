package com.rebane2001.aimobs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class AIMobsCommand {

    public static void setupAIMobsCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("aimobs")
                .executes(AIMobsCommand::status)
                .then(literal("help").executes(AIMobsCommand::help))
                .then(literal("setkey")
                        .then(argument("key", StringArgumentType.string())
                                .executes(AIMobsCommand::setAPIKey)
                        ))
                .then(literal("setvoicekey")
                        .then(argument("voicekey", StringArgumentType.string())
                                .executes(AIMobsCommand::setVoiceAPIKey)
                        ))
                .then(literal("setmodel")
                        .then(argument("model", StringArgumentType.string())
                                .executes(AIMobsCommand::setModel)
                        ))
                .then(literal("settemp")
                        .then(argument("temperature", FloatArgumentType.floatArg(0,1))
                                .executes(AIMobsCommand::setTemp)
                        ))
                .then(literal("enable").executes(context -> setEnabled(context, true)))
                .then(literal("disable").executes(context -> setEnabled(context, false)))
        );
    }
    public static int setEnabled(CommandContext<FabricClientCommandSource> context, boolean enabled) {
        AIMobsConfig.config.enabled = enabled;
        AIMobsConfig.saveConfig();
        context.getSource().sendFeedback(Text.of("AIMobs " + (enabled ? "enabled" : "disabled")));
        return 1;
    }

    public static int status(CommandContext<FabricClientCommandSource> context) {
        boolean hasKey = AIMobsConfig.config.apiKey.length() > 0;
        boolean hasVoiceKey = AIMobsConfig.config.voiceApiKey.length() > 0;
        Text yes = Text.literal("Yes").formatted(Formatting.GREEN);
        Text no = Text.literal("No").formatted(Formatting.RED);
        Text helpText = Text.literal("")
                .append(Text.literal("AIMobs").formatted(Formatting.UNDERLINE))
                .append("").formatted(Formatting.RESET)
                .append("\nEnabled: ").append(AIMobsConfig.config.enabled ? yes : no)
                .append("\nAPI Key: ").append(hasKey ? yes : no)
                .append("\nVoice API Key: ").append(hasVoiceKey ? yes : no)
                .append("\nModel: ").append(AIMobsConfig.config.model)
                .append("\nTemp: ").append(String.valueOf(AIMobsConfig.config.temperature))
                .append("\n\nUse ").append(Text.literal("/aimobs help").formatted(Formatting.GRAY)).append(" for help");
        context.getSource().sendFeedback(helpText);
        return 1;
    }

    public static int help(CommandContext<FabricClientCommandSource> context) {
        Text helpText = Text.literal("")
                .append("AIMobs Commands").formatted(Formatting.UNDERLINE)
                .append("").formatted(Formatting.RESET)
                .append("\n/aimobs - View configuration status")
                .append("\n/aimobs help - View commands help")
                .append("\n/aimobs enable/disable - Enable/disable the mod")
                .append("\n/aimobs setkey <key> - Set OpenAI API key")
                .append("\n/aimobs setvoicekey <voicekey> - Set Google Speech API key")
                .append("\n/aimobs setmodel <model> - Set AI model")
                .append("\n/aimobs settemp <temperature> - Set model temperature")
                .append("\nYou can talk to mobs by shift-clicking on them!");
        context.getSource().sendFeedback(helpText);
        return 1;
    }
    public static int setAPIKey(CommandContext<FabricClientCommandSource> context) {
        String apiKey = context.getArgument("key", String.class);
        if (apiKey.length() > 0) {
            AIMobsConfig.config.apiKey = apiKey;
            AIMobsConfig.saveConfig();
            context.getSource().sendFeedback(Text.of("API key set"));
            return 1;
        }
        return 0;
    }
    public static int setVoiceAPIKey(CommandContext<FabricClientCommandSource> context) {
        String voiceApiKey = context.getArgument("voicekey", String.class);
        if (voiceApiKey.length() > 0) {
            AIMobsConfig.config.voiceApiKey = voiceApiKey;
            AIMobsConfig.saveConfig();
            context.getSource().sendFeedback(Text.of("Voice API key set"));
            return 1;
        }
        return 0;
    }

    public static int setModel(CommandContext<FabricClientCommandSource> context) {
        String model = context.getArgument("model", String.class);
        if (model.length() > 0) {
            AIMobsConfig.config.model = model;
            AIMobsConfig.saveConfig();
            context.getSource().sendFeedback(Text.of("Model set"));
            return 1;
        }
        return 0;
    }
    public static int setTemp(CommandContext<FabricClientCommandSource> context) {
        AIMobsConfig.config.temperature = context.getArgument("temperature", float.class);
        AIMobsConfig.saveConfig();
        context.getSource().sendFeedback(Text.of("Temperature set"));
        return 1;
    }
}
