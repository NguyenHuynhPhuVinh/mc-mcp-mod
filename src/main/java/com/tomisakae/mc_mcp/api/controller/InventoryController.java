package com.tomisakae.mc_mcp.api.controller;

import com.tomisakae.mc_mcp.api.ApiResponse;
import com.tomisakae.mc_mcp.api.service.InventoryService;
import com.tomisakae.mc_mcp.api.util.ApiResponseUtil;
import com.tomisakae.mc_mcp.api.util.MinecraftServerProvider;
import com.tomisakae.mc_mcp.Mcmcpmod;

import io.javalin.http.Context;
import net.minecraft.server.MinecraftServer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        MinecraftServer server = MinecraftServerProvider.getServer();
        if (server == null) {
            ctx.status(503);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Server Minecraft chưa sẵn sàng", null);
            return;
        }
        
        // Lấy tên người chơi từ body
        String playerName = null;
        try {
            String body = ctx.body();
            if (body != null && !body.isEmpty()) {
                JsonObject requestBody = JsonParser.parseString(body).getAsJsonObject();
                if (requestBody.has("playerName")) {
                    playerName = requestBody.get("playerName").getAsString();
                }
            }
        } catch (Exception e) {
            ctx.status(400);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Body không hợp lệ: " + e.getMessage(), null);
            return;
        }
        
        if (playerName == null || playerName.isEmpty()) {
            ctx.status(400);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Tên người chơi không được để trống", null);
            return;
        }
        
        // Lấy thông tin túi đồ của người chơi
        JsonObject inventory = inventoryService.getPlayerInventory(playerName, server);
        
        if (inventory == null) {
            ctx.status(404);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Không tìm thấy người chơi: " + playerName, null);
            return;
        }
        
        // Trả về thông tin túi đồ dưới dạng JSON
        ApiResponseUtil.sendJsonResponse(ctx, true, "Lấy thông tin túi đồ thành công", inventory);
    }
}
