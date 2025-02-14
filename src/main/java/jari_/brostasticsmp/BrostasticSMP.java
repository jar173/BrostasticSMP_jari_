package jari_.brostasticsmp;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;

import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrostasticSMP implements ModInitializer {
	public static final String MOD_ID = "brostasticsmp";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static boolean gesperrt = false;

	@Override
	public void onInitialize() {
	RoleCommand.register();
	BanCommand.register();
	LockdownCommand.register();
	}



}