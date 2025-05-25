package com.tomisakae.mc_mcp.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tomisakae.mc_mcp.Mcmcpmod;

/**
 * Lớp tiện ích để xử lý JSON sử dụng Gson
 */
public class JsonUtils {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    
    /**
     * Chuyển đổi đối tượng thành chuỗi JSON
     * 
     * @param object Đối tượng cần chuyển đổi
     * @return Chuỗi JSON
     */
    public static String toJson(Object object) {
        try {
            return GSON.toJson(object);
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi chuyển đổi đối tượng thành JSON", e);
            return "{}";
        }
    }
    
    /**
     * Chuyển đổi chuỗi JSON thành đối tượng
     * 
     * @param <T> Kiểu dữ liệu của đối tượng
     * @param json Chuỗi JSON
     * @param classOfT Lớp của đối tượng
     * @return Đối tượng
     */
    public static <T> T fromJson(String json, Class<T> classOfT) {
        try {
            return GSON.fromJson(json, classOfT);
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi chuyển đổi JSON thành đối tượng", e);
            return null;
        }
    }
    
    /**
     * Chuyển đổi JsonElement thành đối tượng
     * 
     * @param <T> Kiểu dữ liệu của đối tượng
     * @param jsonElement JsonElement
     * @param classOfT Lớp của đối tượng
     * @return Đối tượng
     */
    public static <T> T fromJson(JsonElement jsonElement, Class<T> classOfT) {
        try {
            return GSON.fromJson(jsonElement, classOfT);
        } catch (Exception e) {
            Mcmcpmod.LOGGER.error("Lỗi khi chuyển đổi JsonElement thành đối tượng", e);
            return null;
        }
    }
    
    /**
     * Tạo JsonObject mới
     * 
     * @return JsonObject mới
     */
    public static JsonObject createJsonObject() {
        return new JsonObject();
    }
    
    /**
     * Lấy Gson instance
     * 
     * @return Gson instance
     */
    public static Gson getGson() {
        return GSON;
    }
}
