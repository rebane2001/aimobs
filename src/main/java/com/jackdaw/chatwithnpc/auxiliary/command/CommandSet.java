package com.jackdaw.chatwithnpc.auxiliary.command;

import com.jackdaw.chatwithnpc.auxiliary.configuration.SettingManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;

public class CommandSet {

    public static void setupCommand(@NotNull CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("npchat")
                .executes(CommandSet::status)
                .then(literal("help").executes(CommandSet::help))
                .then(literal("setkey")
                        .then(argument("key", StringArgumentType.string())
                                .executes(CommandSet::setAPIKey)
                        ))
                .then(literal("setmodel")
                        .then(argument("model", StringArgumentType.string())
                                .executes(CommandSet::setModel)
                        ))
                .then(literal("settemp")
                        .then(argument("temperature", FloatArgumentType.floatArg(0,1))
                                .executes(CommandSet::setTemp)
                        ))
                .then(literal("enable").executes(context -> setEnabled(context, true)))
                .then(literal("disable").executes(context -> setEnabled(context, false)))
        );
    }
    public static int setEnabled(CommandContext<FabricClientCommandSource> context, boolean enabled) {
        SettingManager.enabled = enabled;
        SettingManager.saveConfig();
        context.getSource().sendFeedback(Text.of("ChatWithNPC " + (enabled ? "enabled" : "disabled")));
        return 1;
    }

    public static int status(CommandContext<FabricClientCommandSource> context) {
        boolean hasKey = !SettingManager.apiKey.isEmpty();
        Text yes = Text.literal("Yes").formatted(Formatting.GREEN);
        Text no = Text.literal("No").formatted(Formatting.RED);
        Text helpText = Text.literal("")
                .append(Text.literal("ChatWithNPC").formatted(Formatting.UNDERLINE))
                .append("").formatted(Formatting.RESET)
                .append("\nEnabled: ").append(SettingManager.enabled ? yes : no)
                .append("\nAPI Key: ").append(hasKey ? yes : no)
                .append("\nModel: ").append(SettingManager.model)
                .append("\nTemp: ").append(String.valueOf(SettingManager.temperature))
                .append("\n\nUse ").append(Text.literal("/npchat help").formatted(Formatting.GRAY)).append(" for help");
        context.getSource().sendFeedback(helpText);
        return 1;
    }

    public static int help(CommandContext<FabricClientCommandSource> context) {
        Text helpText = Text.literal("")
                .append("ChatWithNPC Commands").formatted(Formatting.UNDERLINE)
                .append("").formatted(Formatting.RESET)
                .append("\n/npchat - View configuration status")
                .append("\n/npchat help - View commands help")
                .append("\n/npchat enable/disable - Enable/disable the mod")
                .append("\n/npchat setkey <key> - Set OpenAI API key")
                .append("\n/npchat setmodel <model> - Set AI model")
                .append("\n/npchat settemp <temperature> - Set model temperature")
                .append("\nYou can talk to mobs by shift-clicking on them!");
        context.getSource().sendFeedback(helpText);
        return 1;
    }
    public static int setAPIKey(CommandContext<FabricClientCommandSource> context) {
        String apiKey = context.getArgument("key", String.class);
        if (!apiKey.isEmpty()) {
            SettingManager.apiKey = apiKey;
            SettingManager.saveConfig();
            context.getSource().sendFeedback(Text.of("API key set"));
            return 1;
        }
        return 0;
    }
    public static int setModel(CommandContext<FabricClientCommandSource> context) {
        String model = context.getArgument("model", String.class);
        if (!model.isEmpty()) {
            SettingManager.model = model;
            SettingManager.saveConfig();
            context.getSource().sendFeedback(Text.of("Model set"));
            return 1;
        }
        return 0;
    }
    public static int setTemp(CommandContext<FabricClientCommandSource> context) {
        SettingManager.temperature = context.getArgument("temperature", float.class);
        SettingManager.saveConfig();
        context.getSource().sendFeedback(Text.of("Temperature set"));
        return 1;
    }
}
