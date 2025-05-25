package com.tomisakae.mc_mcp;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tomisakae.mc_mcp.api.ApiServer;
import com.tomisakae.mc_mcp.api.util.MinecraftServerProvider;

public class Mcmcpmod implements ModInitializer {
	public static final String MOD_ID = "mc-mcp-mod";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// Biến để theo dõi trạng thái API server
	private static boolean apiServerInitialized = false;
	
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Khởi động MC-MCP Mod!");
		
		// Chỉ đăng ký các sự kiện một lần
		if (!apiServerInitialized) {
			// Đăng ký sự kiện để khởi động API server khi Minecraft server khởi động
			ServerLifecycleEvents.SERVER_STARTED.register(server -> {
				LOGGER.info("Minecraft server đã khởi động, bắt đầu khởi động API server...");
				// Lưu trữ instance của MinecraftServer để sử dụng sau này
				MinecraftServerProvider.setServer(server);
				ApiServer.getInstance().start();
			});
			
			// Đăng ký sự kiện để dừng API server khi Minecraft server dừng
			ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
				LOGGER.info("Minecraft server đang dừng, dừng API server...");
				ApiServer.getInstance().stop();
				// Xóa instance của MinecraftServer
				MinecraftServerProvider.clearServer();
			});
			
			apiServerInitialized = true;
		}
	}
}