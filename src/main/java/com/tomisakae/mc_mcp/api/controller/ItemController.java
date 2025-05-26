package com.tomisakae.mc_mcp.api.controller;

import com.tomisakae.mc_mcp.api.ApiResponse;
import com.tomisakae.mc_mcp.api.model.ItemInfo;
import com.tomisakae.mc_mcp.api.model.RecipeInfo;
import com.tomisakae.mc_mcp.api.service.ItemService;
import com.tomisakae.mc_mcp.api.util.ApiResponseUtil;
import com.tomisakae.mc_mcp.api.util.MinecraftServerProvider;

import io.javalin.http.Context;
import net.minecraft.server.MinecraftServer;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

/**
 * Controller xử lý các request liên quan đến vật phẩm và công thức chế tạo
 */
public class ItemController {
    
    private final ItemService itemService;
    
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }
    
    /**
     * Xử lý request lấy danh sách tất cả vật phẩm trong game
     * 
     * @param ctx Context của request
     */
    public void getAllItems(Context ctx) {
        MinecraftServer server = MinecraftServerProvider.getServer();
        if (server == null) {
            ctx.status(503);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Server Minecraft chưa sẵn sàng", null);
            return;
        }
        
        try {
            List<String> itemIds = itemService.getAllItems(server);
            // Chuyển đổi danh sách thành JsonArray
            Gson gson = new Gson();
            JsonElement jsonItems = gson.toJsonTree(itemIds);
            ApiResponseUtil.sendJsonResponse(ctx, true, "Lấy danh sách vật phẩm thành công", jsonItems);
        } catch (Exception e) {
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lỗi khi lấy danh sách vật phẩm: " + e.getMessage(), null);
        }
    }
    
    /**
     * Xử lý request lấy công thức chế tạo của một vật phẩm cụ thể
     * 
     * @param ctx Context của request
     */
    public void getRecipesForItem(Context ctx) {
        MinecraftServer server = MinecraftServerProvider.getServer();
        if (server == null) {
            ctx.status(503);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Server Minecraft chưa sẵn sàng", null);
            return;
        }
        
        // Lấy ID vật phẩm từ body
        String itemId = null;
        
        try {
            String body = ctx.body();
            if (body != null && !body.isEmpty()) {
                JsonObject requestBody = JsonParser.parseString(body).getAsJsonObject();
                if (requestBody.has("itemId")) {
                    itemId = requestBody.get("itemId").getAsString();
                }
            }
        } catch (Exception e) {
            ctx.status(400);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Body không hợp lệ: " + e.getMessage(), null);
            return;
        }
        
        if (itemId == null || itemId.isEmpty()) {
            ctx.status(400);
            ApiResponseUtil.sendJsonResponse(ctx, false, "ID vật phẩm không được để trống", null);
            return;
        }
        
        try {
            List<RecipeInfo> recipes = itemService.getRecipesForItem(server, itemId);
            if (recipes.isEmpty()) {
                ApiResponseUtil.sendJsonResponse(ctx, true, "Không tìm thấy công thức chế tạo cho vật phẩm: " + itemId, null);
            } else {
                // Chuyển đổi danh sách thành JsonArray
                Gson gson = new Gson();
                JsonElement jsonRecipes = gson.toJsonTree(recipes);
                ApiResponseUtil.sendJsonResponse(ctx, true, "Lấy công thức chế tạo thành công", jsonRecipes);
            }
        } catch (Exception e) {
            ctx.status(500);
            ApiResponseUtil.sendJsonResponse(ctx, false, "Lỗi khi lấy công thức chế tạo: " + e.getMessage(), null);
        }
    }
}
