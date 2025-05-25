package com.tomisakae.mc_mcp.api.controller;

import com.google.gson.JsonObject;
import com.tomisakae.mc_mcp.api.ApiResponse;
import com.tomisakae.mc_mcp.api.service.WorldService;
import com.tomisakae.mc_mcp.api.util.ApiResponseUtil;
import com.tomisakae.mc_mcp.api.util.MinecraftServerProvider;
import com.tomisakae.mc_mcp.Mcmcpmod;

import io.javalin.http.Context;
import net.minecraft.server.MinecraftServer;

/**
 * Controller xử lý các request liên quan đến thông tin thế giới
 */
public class WorldController {
    private final WorldService worldService;

    public WorldController(WorldService worldService) {
        this.worldService = worldService;
    }

    /**
     * Lấy thông tin chung về thế giới
     * 
     * @param ctx Context của request
     */
    public void getWorldInfo(Context ctx) {
        MinecraftServer server = MinecraftServerProvider.getServer();
        if (server == null) {
            ctx.status(503);
            ctx.json(new ApiResponse<>(false, "Server Minecraft chưa sẵn sàng", null));
            return;
        }

        try {
            JsonObject worldInfo = worldService.getWorldInfo(server);
            ApiResponseUtil.sendJsonResponse(ctx, true, "Lấy thông tin thế giới thành công", worldInfo);
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi lấy thông tin thế giới: " + e.getMessage(), e);
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lỗi khi lấy thông tin thế giới: " + e.getMessage(), null);
        }
    }
}
