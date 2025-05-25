package com.tomisakae.mc_mcp.api.util;

import net.minecraft.server.MinecraftServer;
import com.tomisakae.mc_mcp.Mcmcpmod;

/**
 * Lớp tiện ích để cung cấp instance của MinecraftServer
 */
public class MinecraftServerProvider {
    private static MinecraftServer server;

    /**
     * Đặt instance của MinecraftServer
     * 
     * @param minecraftServer Instance của MinecraftServer
     */
    public static void setServer(MinecraftServer minecraftServer) {
        server = minecraftServer;
        Mcmcpmod.LOGGER.info("MinecraftServer đã được đăng ký với MinecraftServerProvider");
    }

    /**
     * Lấy instance của MinecraftServer
     * 
     * @return Instance của MinecraftServer hoặc null nếu chưa được đặt
     */
    public static MinecraftServer getServer() {
        return server;
    }

    /**
     * Xóa instance của MinecraftServer
     */
    public static void clearServer() {
        server = null;
        Mcmcpmod.LOGGER.info("MinecraftServer đã bị xóa khỏi MinecraftServerProvider");
    }
}
