package jari_.brostasticsmp;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Date;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Arrays;
import net.minecraft.server.*;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BanCommand {

    private static Logger LOGGER = LoggerFactory.getLogger("BANCOMMAND");

    private static final Pattern TIME_PATTERN = Pattern.compile("^(\\d+)([hdwmy])$");
    private static final String[] TIME_UNITS = new String[]{"h", "d", "w", "m", "y"};

    private static final SuggestionProvider<ServerCommandSource> DURATION_SUGGESTIONS = (context, builder) -> {
        String input = builder.getRemaining();
        if (input.matches("\\d+")) {
            Arrays.stream(TIME_UNITS).forEach(unit ->
                    builder.suggest(input + unit)
            );
        }
        return builder.buildFuture();
    };

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("ModBan")
                        .then(CommandManager.argument("TargetPlayer", EntityArgumentType.player())
                                .then(CommandManager.argument("Reason", StringArgumentType.string())
                                        .then(CommandManager.argument("Duration", StringArgumentType.word())
                                                .suggests(DURATION_SUGGESTIONS)
                                                .executes(BanCommand::execute)
                                        )
                                        .executes(BanCommand::executepermanent)
                                )
                        )
        ));
    }

    private static int executepermanent(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String argumentreason = StringArgumentType.getString(context, "Reason");
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "TargetPlayer");

        MinecraftServer server = context.getSource().getServer();
        PlayerManager playerManager = context.getSource().getServer().getPlayerManager();
        BannedPlayerList bannedPlayerList = playerManager.getUserBanList();
        GameProfile profile = targetPlayer.getGameProfile();

        String banreason = "§4" + argumentreason;

        String bansource = context.getSource().getPlayer().toString();
        BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(profile, null, bansource, null,"§4" + banreason );
        bannedPlayerList.add(bannedPlayerEntry);

        ServerPlayNetworkHandler serverPlayNetworkHandler = context.getSource().getPlayer().networkHandler;
        Text reason = Text.literal( banreason);
        serverPlayNetworkHandler.disconnect(reason);

        return 1;

    }


    private static Date parseDuration(String duration) {
        LOGGER.info("parseDuration executes");
        Matcher matcher = TIME_PATTERN.matcher(duration.toLowerCase());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid duration format. Use: <number><unit> where unit is h(hours), d(days), w(weeks), m(months), y(years)");
        }

        int amount = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2);
        long currentTime = System.currentTimeMillis();
        long multiplier = switch (unit) {
            case "h" -> 1000L * 60L * 60L;         // hours
            case "d" -> 1000L * 60L * 60L * 24L;   // days
            case "w" -> 1000L * 60L * 60L * 24L * 7L; // weeks
            case "m" -> 1000L * 60L * 60L * 24L * 30L; // months (approximate)
            case "y" -> 1000L * 60L * 60L * 24L * 365L; // years
            default -> throw new IllegalArgumentException("Invalid time unit");
        };

        return new Date(currentTime + (amount * multiplier));
    }



    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        LOGGER.info("Command Execute");
        String argumentreason = StringArgumentType.getString(context, "Reason");
        ServerPlayerEntity targetPlayer = EntityArgumentType.getPlayer(context, "TargetPlayer");
        String duration = StringArgumentType.getString(context, "Duration");

        MinecraftServer server = context.getSource().getServer();
        PlayerManager playerManager = context.getSource().getServer().getPlayerManager();
        BannedPlayerList bannedPlayerList = playerManager.getUserBanList();
        GameProfile profile = targetPlayer.getGameProfile();

        String banreason = "§4" + argumentreason;
        Date expiryDate = parseDuration(duration);

            String bansource = context.getSource().getPlayer().toString();
            BannedPlayerEntry bannedPlayerEntry = new BannedPlayerEntry(profile, null, context.getSource().getName(), expiryDate,"§4" + banreason );
            bannedPlayerList.add(bannedPlayerEntry);

        ServerPlayNetworkHandler serverPlayNetworkHandler = context.getSource().getPlayer().networkHandler;
        Text reason = Text.literal("§4  You are banned from this Server! Reason: " + banreason);
        playerManager.getPlayer(targetPlayer.getUuid()).networkHandler.disconnect(Text.literal(String.valueOf(reason)));
        BrostasticSMP.gesperrt = true;
        return 1;
    }
}
