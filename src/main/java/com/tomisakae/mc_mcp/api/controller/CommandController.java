package com.tomisakae.mc_mcp.api.controller;

import com.tomisakae.mc_mcp.api.ApiResponse;
import com.tomisakae.mc_mcp.api.util.MinecraftServerProvider;
import com.tomisakae.mc_mcp.Mcmcpmod;

import io.javalin.http.Context;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Map;

/**
 * Controller xử lý các request liên quan đến việc thực thi lệnh
 */
public class CommandController {

    /**
     * Xử lý request thực thi lệnh Minecraft
     * 
     * @param ctx Context của request
     */
    public void executeCommand(Context ctx) {
        // Khai báo biến command
        String command;
        
        // Log body request để debug
        String rawBody = ctx.body();
        Mcmcpmod.LOGGER.info("Body request nhận được: " + rawBody);
        
        try {
            // Đọc từ JSON body sử dụng Map - cách đã hoạt động
            Map<String, Object> jsonMap = ctx.bodyAsClass(Map.class);
            if (jsonMap.containsKey("command")) {
                command = jsonMap.get("command").toString();
                Mcmcpmod.LOGGER.info("Đọc command từ JSON body: " + command);
            } else {
                ctx.status(400);
                ctx.json(new ApiResponse<>(false, "Body request không có trường 'command'", null));
                return;
            }
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi xử lý JSON body: " + e.getMessage(), e);
            ctx.status(400);
            ctx.json(new ApiResponse<>(false, "Body request không hợp lệ. Cần gửi JSON với trường 'command'.", null));
            return;
        }
        
        // Kiểm tra nếu command rỗng
        if (command == null || command.isEmpty()) {
            ctx.status(400);
            ctx.json(new ApiResponse<>(false, "Lệnh không được để trống", null));
            return;
        }
        
        Mcmcpmod.LOGGER.info("Command sẽ được thực thi: " + command);
        
        MinecraftServer server = MinecraftServerProvider.getServer();
        if (server == null) {
            ctx.status(503);
            ctx.json(new ApiResponse<>(false, "Server Minecraft chưa sẵn sàng", null));
            return;
        }
        
        try {
            // Lấy ServerCommandSource từ server
            ServerCommandSource commandSource = server.getCommandSource();
            
            // Thực thi lệnh - executeWithPrefix trả về void
            server.getCommandManager().executeWithPrefix(commandSource, command);
            
            // Trả về kết quả thành công
            ctx.json(new ApiResponse<>(true, "Lệnh đã được thực thi", command));
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi thực thi lệnh: " + e.getMessage(), e);
            ctx.status(500);
            ctx.json(new ApiResponse<>(false, "Lỗi khi thực thi lệnh: " + e.getMessage(), null));
        }
    }
}
