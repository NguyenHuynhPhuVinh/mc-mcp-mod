package com.tomisakae.mc_mcp.api.controller;

import com.tomisakae.mc_mcp.api.ApiResponse;
import com.tomisakae.mc_mcp.api.service.BaritoneService;
import com.tomisakae.mc_mcp.api.util.ApiResponseUtil;
import com.tomisakae.mc_mcp.api.util.MinecraftServerProvider;
import com.tomisakae.mc_mcp.api.util.PlayerUtil;
import com.tomisakae.mc_mcp.Mcmcpmod;

import io.javalin.http.Context;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Controller xử lý các request liên quan đến Baritone
 */
public class BaritoneController {
    
    private final BaritoneService baritoneService;
    
    public BaritoneController() {
        this.baritoneService = new BaritoneService();
    }
    
    /**
     * Xử lý request thực thi lệnh Baritone cho người chơi
     * 
     * @param ctx Context của request
     */
    public void executeBaritoneCommand(Context ctx) {
        MinecraftServer server = MinecraftServerProvider.getServer();
        if (server == null) {
            ctx.status(503);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Server Minecraft chưa sẵn sàng", null);
            return;
        }
        
        // Lấy lệnh từ body
        String playerName = null;
        String command = null;
        
        try {
            String body = ctx.body();
            if (body != null && !body.isEmpty()) {
                JsonObject requestBody = JsonParser.parseString(body).getAsJsonObject();
                if (requestBody.has("playerName")) {
                    playerName = requestBody.get("playerName").getAsString();
                }
                if (requestBody.has("command")) {
                    command = requestBody.get("command").getAsString();
                }
            }
        } catch (Exception e) {
            ctx.status(400);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Body không hợp lệ: " + e.getMessage(), null);
            return;
        }
        
        // Sử dụng tên người chơi mặc định nếu không có trong body
        playerName = PlayerUtil.getPlayerName(playerName, server);
        
        if (playerName == null || playerName.isEmpty()) {
            ctx.status(400);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Không tìm thấy người chơi mặc định và không có tên người chơi được cung cấp", null);
            return;
        }
        
        if (command == null || command.isEmpty()) {
            ctx.status(400);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lệnh Baritone không được để trống", null);
            return;
        }
        
        // Tìm người chơi theo tên
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerName);
        if (player == null) {
            ctx.status(404);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Không tìm thấy người chơi: " + playerName, null);
            return;
        }
        
        // Thực thi lệnh Baritone
        try {
            JsonObject result = baritoneService.executeBaritoneCommand(player, command);
            ApiResponseUtil.sendJsonResponse(ctx, true, "Lệnh Baritone đã được thực thi thành công", result);
        } catch (Exception e) {
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lỗi khi thực thi lệnh Baritone: " + e.getMessage(), null);
        }
    }
    
    /**
     * Xử lý request lấy danh sách lệnh Baritone hỗ trợ
     * 
     * @param ctx Context của request
     */
    public void getBaritoneCommands(Context ctx) {
        try {
            JsonObject commands = baritoneService.getBaritoneCommands();
            ApiResponseUtil.sendJsonResponse(ctx, true, "Lấy danh sách lệnh Baritone thành công", commands);
        } catch (Exception e) {
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lỗi khi lấy danh sách lệnh Baritone: " + e.getMessage(), null);
        }
    }
}
