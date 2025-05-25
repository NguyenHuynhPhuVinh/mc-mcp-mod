package com.tomisakae.mc_mcp.api.controller;

import com.tomisakae.mc_mcp.api.ApiResponse;
import com.tomisakae.mc_mcp.api.service.InventoryService;
import com.tomisakae.mc_mcp.api.util.MinecraftServerProvider;
import com.tomisakae.mc_mcp.Mcmcpmod;

import io.javalin.http.Context;
import net.minecraft.server.MinecraftServer;
import com.google.gson.JsonObject;

/**
 * Controller xử lý các request liên quan đến túi đồ của người chơi
 */
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    /**
     * Xử lý request lấy thông tin túi đồ của người chơi dưới dạng JSON
     * 
     * @param ctx Context của request
     */
    public void getPlayerInventoryAsJson(Context ctx) {
        String playerName = ctx.pathParam("playerName");
        
        if (playerName == null || playerName.isEmpty()) {
            ctx.status(400);
            ctx.json(new ApiResponse<>(false, "Tên người chơi không được để trống", null));
            return;
        }
        
        MinecraftServer server = MinecraftServerProvider.getServer();
        if (server == null) {
            ctx.status(503);
            ctx.json(new ApiResponse<>(false, "Server Minecraft chưa sẵn sàng", null));
            return;
        }
        
        // Lấy thông tin túi đồ của người chơi
        Object inventory = inventoryService.getPlayerInventory(playerName, server);
        
        if (inventory == null) {
            ctx.status(404);
            ctx.json(new ApiResponse<>(false, "Không tìm thấy người chơi: " + playerName, null));
            return;
        }
        
        // Trả về trực tiếp đối tượng Java thay vì JsonObject của Gson
        ctx.json(new ApiResponse<>(true, "Lấy thông tin túi đồ thành công", inventory));
    }
}
