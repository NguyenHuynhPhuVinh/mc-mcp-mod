package com.tomisakae.mc_mcp.api;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomisakae.mc_mcp.Mcmcpmod;
import com.tomisakae.mc_mcp.api.controller.InventoryController;
import com.tomisakae.mc_mcp.api.service.InventoryService;
import com.tomisakae.mc_mcp.api.config.ApiConfig;
import com.tomisakae.mc_mcp.api.exception.ApiExceptionHandler;

/**
 * Lớp quản lý máy chủ API Javalin
 */
public class ApiServer {
    private final Javalin app;
    private static ApiServer instance;
    private final ApiConfig config;
    private final InventoryController inventoryController;

    private ApiServer(ApiConfig config) {
        this.config = config;
        
        // Cấu hình ObjectMapper cho Jackson
        ObjectMapper objectMapper = new ObjectMapper();
        
        this.app = Javalin.create(javalinConfig -> {
            // Cấu hình CORS nếu được bật
            if (config.isEnableCors()) {
                javalinConfig.plugins.enableCors(cors -> cors.add(it -> {
                    it.anyHost();
                }));
            }
            
            // Cấu hình JSON mapper
            javalinConfig.jsonMapper(new JavalinJackson(objectMapper));
        }).exception(Exception.class, new ApiExceptionHandler());
        
        // Khởi tạo các service và controller
        InventoryService inventoryService = new InventoryService();
        this.inventoryController = new InventoryController(inventoryService);
        
        // Đăng ký các endpoint
        registerRoutes();
    }

    /**
     * Lấy instance của ApiServer (Singleton pattern)
     */
    public static ApiServer getInstance() {
        if (instance == null) {
            instance = new ApiServer(ApiConfig.defaultConfig());
        }
        return instance;
    }
    
    /**
     * Lấy instance của ApiServer với cấu hình tùy chỉnh
     * 
     * @param config Cấu hình cho API server
     * @return Instance của ApiServer
     */
    public static ApiServer getInstance(ApiConfig config) {
        if (instance == null) {
            instance = new ApiServer(config);
        } else {
            Mcmcpmod.LOGGER.warn("ApiServer đã được khởi tạo với cấu hình khác. Cấu hình mới sẽ không được áp dụng.");
        }
        return instance;
    }

    /**
     * Đăng ký các route cho API
     */
    private void registerRoutes() {
        // API endpoint để kiểm tra server hoạt động
        app.get("/api/health", this::healthCheck);
        
        // Đăng ký route cho inventory
        app.get("/api/player/{playerName}/inventory", inventoryController::getPlayerInventoryAsJson);
    }

    /**
     * Endpoint kiểm tra server hoạt động
     */
    private void healthCheck(Context ctx) {
        ctx.json(new ApiResponse<>(true, "API server is running", null));
    }

    /**
     * Khởi động server API
     */
    public void start() {
        try {
            app.start(config.getPort());
            Mcmcpmod.LOGGER.info("API server started on port " + config.getPort());
        } catch (IllegalStateException e) {
            // Nếu server đã được khởi động, tạo một instance mới
            if (e.getMessage().contains("Server already started")) {
                Mcmcpmod.LOGGER.info("API server đã được khởi động trước đó, tạo một instance mới");
                instance = new ApiServer(config);
                instance.app.start(config.getPort());
                Mcmcpmod.LOGGER.info("API server mới đã được khởi động trên port " + config.getPort());
            } else {
                // Nếu là lỗi khác, ghi log và ném lại ngoại lệ
                Mcmcpmod.LOGGER.error("Lỗi khi khởi động API server: " + e.getMessage());
                throw e;
            }
        }
    }

    /**
     * Dừng server API
     */
    public void stop() {
        if (app != null) {
            try {
                app.stop();
                Mcmcpmod.LOGGER.info("API server stopped");
            } catch (Exception e) {
                // Ghi log lỗi nhưng không ném ngoại lệ
                Mcmcpmod.LOGGER.error("Lỗi khi dừng API server: " + e.getMessage());
            }
        }
    }
}
