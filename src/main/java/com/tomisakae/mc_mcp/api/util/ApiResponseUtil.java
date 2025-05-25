package com.tomisakae.mc_mcp.api.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tomisakae.mc_mcp.api.ApiResponse;
import io.javalin.http.Context;

/**
 * Lớp tiện ích để xử lý phản hồi API
 */
public class ApiResponseUtil {

    /**
     * Gửi phản hồi JSON với JsonObject
     * 
     * @param ctx Context của request
     * @param success Trạng thái thành công
     * @param message Thông báo
     * @param data Dữ liệu JSON
     */
    public static void sendJsonResponse(Context ctx, boolean success, String message, JsonElement data) {
        // Tạo một đối tượng JSON mới
        JsonObject response = new JsonObject();
        response.addProperty("success", success);
        response.addProperty("message", message);
        if (data != null) {
            response.add("data", data);
        } else {
            response.add("data", null);
        }
        
        // Đặt kiểu nội dung và gửi phản hồi
        ctx.contentType("application/json");
        ctx.result(response.toString());
    }
    
    /**
     * Gửi phản hồi JSON với đối tượng thông thường
     * 
     * @param ctx Context của request
     * @param response Đối tượng phản hồi
     */
    public static <T> void sendResponse(Context ctx, ApiResponse<T> response) {
        ctx.json(response);
    }
}
