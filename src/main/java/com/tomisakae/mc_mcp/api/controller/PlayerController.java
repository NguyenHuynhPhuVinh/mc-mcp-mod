package com.tomisakae.mc_mcp.api.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tomisakae.mc_mcp.api.ApiResponse;
import com.tomisakae.mc_mcp.api.service.PlayerService;
import com.tomisakae.mc_mcp.api.util.ApiResponseUtil;
import com.tomisakae.mc_mcp.api.util.MinecraftServerProvider;
import com.tomisakae.mc_mcp.Mcmcpmod;

import io.javalin.http.Context;
import net.minecraft.server.MinecraftServer;

/**
 * Controller xử lý các request liên quan đến thông tin người chơi
 */
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    /**
     * Lấy thông tin chi tiết về người chơi
     * 
     * @param ctx Context của request
     */
    public void getPlayerInfo(Context ctx) {
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

        try {
            JsonObject playerInfo = playerService.getPlayerInfo(playerName, server);
            if (playerInfo == null) {
                ctx.status(404);
                ApiResponseUtil.sendJsonResponse(ctx, false, "Không tìm thấy người chơi: " + playerName, null);
                return;
            }
            ApiResponseUtil.sendJsonResponse(ctx, true, "Lấy thông tin người chơi thành công", playerInfo);
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi lấy thông tin người chơi: " + e.getMessage(), e);
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lỗi khi lấy thông tin người chơi: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy thông tin về các block xung quanh người chơi
     * 
     * @param ctx Context của request
     */
    public void getPlayerSurroundings(Context ctx) {
        MinecraftServer server = MinecraftServerProvider.getServer();
        
        if (server == null) {
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Không thể kết nối với server Minecraft", null);
            return;
        }

        // Lấy tham số từ body, mặc định là 15
        String playerName = null;
        int radius = 15;
        int verticalRadius = 30;
        boolean includeCommonBlocks = false;
        
        try {
            // Đọc body của request
            String body = ctx.body();
            if (body != null && !body.isEmpty()) {
                JsonObject requestBody = JsonParser.parseString(body).getAsJsonObject();
                
                // Lấy tên người chơi
                if (requestBody.has("playerName")) {
                    playerName = requestBody.get("playerName").getAsString();
                }
                
                // Lấy bán kính nếu có
                if (requestBody.has("radius")) {
                    radius = requestBody.get("radius").getAsInt();
                    // Giới hạn bán kính tối đa là 30 để tránh quá tải
                    if (radius > 30) {
                        radius = 30;
                    }
                }
                
                // Lấy bán kính dọc nếu có
                if (requestBody.has("verticalRadius")) {
                    verticalRadius = requestBody.get("verticalRadius").getAsInt();
                    // Giới hạn bán kính dọc tối đa là 60
                    if (verticalRadius > 60) {
                        verticalRadius = 60;
                    }
                } else {
                    // Mặc định bán kính dọc gấp đôi bán kính ngang
                    verticalRadius = radius * 2;
                }
                
                // Có bao gồm các block phổ biến không
                if (requestBody.has("includeCommonBlocks")) {
                    includeCommonBlocks = requestBody.get("includeCommonBlocks").getAsBoolean();
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

        try {
            JsonObject surroundings = playerService.getPlayerSurroundings(playerName, radius, server, verticalRadius, includeCommonBlocks);
            if (surroundings == null) {
                ctx.status(404);
                ApiResponseUtil.sendJsonResponse(ctx, false, "Không tìm thấy người chơi: " + playerName, null);
                return;
            }
            ApiResponseUtil.sendJsonResponse(ctx, true, "Lấy thông tin xung quanh người chơi thành công", surroundings);
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi lấy thông tin xung quanh người chơi: " + e.getMessage(), e);
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lỗi khi lấy thông tin xung quanh người chơi: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy danh sách entity gần người chơi
     * 
     * @param ctx Context của request
     */
    public void getNearbyEntities(Context ctx) {
        MinecraftServer server = MinecraftServerProvider.getServer();
        
        if (server == null) {
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Không thể kết nối với server Minecraft", null);
            return;
        }

        // Lấy tham số từ body
        String playerName = null;
        int radius = 10;
        String entityType = null; // Lọc theo loại entity
        boolean includePassive = true; // Có bao gồm entity thụ động
        boolean includeHostile = true; // Có bao gồm entity thù địch
        
        try {
            // Đọc body của request
            String body = ctx.body();
            if (body != null && !body.isEmpty()) {
                JsonObject requestBody = JsonParser.parseString(body).getAsJsonObject();
                
                // Lấy tên người chơi
                if (requestBody.has("playerName")) {
                    playerName = requestBody.get("playerName").getAsString();
                }
                
                // Lấy bán kính nếu có
                if (requestBody.has("radius")) {
                    radius = requestBody.get("radius").getAsInt();
                    // Giới hạn bán kính tối đa là 50 để tránh quá tải
                    if (radius > 50) {
                        radius = 50;
                    }
                }
                
                // Lấy loại entity nếu có
                if (requestBody.has("entityType")) {
                    entityType = requestBody.get("entityType").getAsString();
                }
                
                // Có bao gồm entity thụ động không
                if (requestBody.has("includePassive")) {
                    includePassive = requestBody.get("includePassive").getAsBoolean();
                }
                
                // Có bao gồm entity thù địch không
                if (requestBody.has("includeHostile")) {
                    includeHostile = requestBody.get("includeHostile").getAsBoolean();
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

        try {
            JsonObject entities = playerService.getNearbyEntities(playerName, radius, server, entityType, includePassive, includeHostile);
            if (entities == null) {
                ctx.status(404);
                ApiResponseUtil.sendJsonResponse(ctx, false, "Không tìm thấy người chơi: " + playerName, null);
                return;
            }
            ApiResponseUtil.sendJsonResponse(ctx, true, "Lấy danh sách entity gần người chơi thành công", entities);
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi lấy danh sách entity gần người chơi: " + e.getMessage(), e);
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lỗi khi lấy danh sách entity gần người chơi: " + e.getMessage(), null);
        }
    }

    /**
     * Lấy thống kê (statistics) của người chơi
     * 
     * @param ctx Context của request
     */
    public void getPlayerStatistics(Context ctx) {
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

        try {
            JsonObject statistics = playerService.getPlayerStatistics(playerName, server);
            if (statistics == null) {
                ctx.status(404);
                ApiResponseUtil.sendJsonResponse(ctx, false, "Không tìm thấy người chơi: " + playerName, null);
                return;
            }
            ApiResponseUtil.sendJsonResponse(ctx, true, "Lấy thống kê người chơi thành công", statistics);
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi lấy thống kê người chơi: " + e.getMessage(), e);
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lỗi khi lấy thống kê người chơi: " + e.getMessage(), null);
        }
    }
}
