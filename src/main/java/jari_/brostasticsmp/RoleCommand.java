package jari_.brostasticsmp;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.ColorArgumentType;
import net.minecraft.component.Component;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.network.*;

//say §f§l[§4§lADMIN§f§l] <§4§ljari15732§f§l>§4§l Test
//say §f§l[§4§lADMIN§f§l] §r<§4jari15732§f> Hallo das ist ein test!
//

import java.awt.*;

import static net.minecraft.component.Component.*;

public class RoleCommand  {
    public static void register(){
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) ->commandDispatcher.register(
                CommandManager.literal("role")
                        .then(CommandManager.literal("add")
                            .then(CommandManager.argument("RoleName", StringArgumentType.string())
                                .then(CommandManager.argument("DisplayName", StringArgumentType.string())
                                    .then(CommandManager.argument("Group", StringArgumentType.string())
                                        .then(CommandManager.argument("Herachie", IntegerArgumentType.integer())
                                            .then(CommandManager.argument("Color", ColorArgumentType.color()).executes(commandContext -> {
                                                String roleName = StringArgumentType.getString(commandContext, "RoleName");
                                                String displayName = StringArgumentType.getString(commandContext, "DisplayName");
                                                String group = StringArgumentType.getString(commandContext, "Group");
                                                int hierarchy = IntegerArgumentType.getInteger(commandContext, "Herachie");
                                                Formatting color = ColorArgumentType.getColor(commandContext, "Color");
                                                return 1;
                                                    })
                                            )
                                        )
                                    )
                                )
                            )
                        )
                        .then(CommandManager.literal("remove")
                                .then(CommandManager.argument("RoleName", StringArgumentType.string())
                                        .then(CommandManager.argument("Confirm",BoolArgumentType.bool())
                                        )
                                )
                        )
                        .then(CommandManager.literal("edit")
                                .then(CommandManager.literal("Group")
                                        .then(CommandManager.argument("RoleName",StringArgumentType.string())
                                            .then(CommandManager.argument("NewGroup", StringArgumentType.string())
                                            )
                                        )
                                )
                                .then(CommandManager.literal("Color")
                                        .then(CommandManager.argument("NewColor", ColorArgumentType.color())
                                        )
                                )
                                .then(CommandManager.literal("Herachie")
                                        .then(CommandManager.argument("NewHerachie", IntegerArgumentType.integer())
                                        )
                                )
                                .then(CommandManager.literal("Praefix")
                                        .then(CommandManager.argument("NewPraefix",StringArgumentType.string())
                                        )
                                )
                        )
        ));
    }
}
