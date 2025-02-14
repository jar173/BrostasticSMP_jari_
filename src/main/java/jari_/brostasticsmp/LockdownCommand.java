package jari_.brostasticsmp;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
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

public class LockdownCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger("LockdownCommand");


    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                CommandManager.literal("Lockdown")
                        .then(CommandManager.argument("Lock", BoolArgumentType.bool())
                                        .executes(LockdownCommand::execute)
                        )
        ));
    }



    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {

        Boolean Lock = BoolArgumentType.getBool(context, "Lock");

        if(Lock == true){
            BrostasticSMP.gesperrt = true;
            context.getSource().getServer().sendMessage(Text.of("Server wurde geschlossen."));
            context.getSource().sendMessage(Text.of("Server wurde Geschlossen."));
        } else {
            context.getSource().getServer().sendMessage(Text.of("Server wurde geöffnet."));
            BrostasticSMP.gesperrt = false;
        }

        return 1;
    }
}
