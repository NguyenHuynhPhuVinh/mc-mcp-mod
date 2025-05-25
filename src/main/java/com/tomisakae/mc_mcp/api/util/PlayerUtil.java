package com.tomisakae.mc_mcp.api.util;

import com.tomisakae.mc_mcp.Mcmcpmod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Tiện ích xử lý các tác vụ liên quan đến người chơi
 */
public class PlayerUtil {
    
    /**
     * Lấy tên người chơi mặc định (chủ server hoặc người chơi đầu tiên)
     * 
     * @param server Server Minecraft
     * @return Tên người chơi mặc định, hoặc null nếu không có người chơi nào online
     */
    public static String getDefaultPlayerName(MinecraftServer server) {
        if (server == null) {
            return null;
        }
        
        // Nếu có người chơi đang online
        if (server.getPlayerManager().getCurrentPlayerCount() > 0) {
            // Lấy người chơi đầu tiên trong danh sách
            ServerPlayerEntity player = server.getPlayerManager().getPlayerList().get(0);
            return player.getName().getString();
        }
        
        return null;
    }
    
    /**
     * Lấy tên người chơi từ request body, nếu không có thì lấy mặc định
     * 
     * @param playerNameFromBody Tên người chơi từ request body
     * @param server Server Minecraft
     * @return Tên người chơi, hoặc null nếu không có người chơi nào
     */
    public static String getPlayerName(String playerNameFromBody, MinecraftServer server) {
        // Nếu tên người chơi được cung cấp trong body, sử dụng nó
        if (playerNameFromBody != null && !playerNameFromBody.isEmpty()) {
            return playerNameFromBody;
        }
        
        // Nếu không, lấy tên người chơi mặc định
        return getDefaultPlayerName(server);
    }
}
