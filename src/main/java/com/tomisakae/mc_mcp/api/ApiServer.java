package com.tomisakae.mc_mcp.api;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.json.JavalinJackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tomisakae.mc_mcp.Mcmcpmod;
import com.tomisakae.mc_mcp.api.controller.InventoryController;
import com.tomisakae.mc_mcp.api.controller.CommandController;
import com.tomisakae.mc_mcp.api.controller.PlayerController;
import com.tomisakae.mc_mcp.api.controller.WorldController;
import com.tomisakae.mc_mcp.api.controller.ItemController;
import com.tomisakae.mc_mcp.api.service.InventoryService;
import com.tomisakae.mc_mcp.api.service.PlayerService;
import com.tomisakae.mc_mcp.api.service.WorldService;
import com.tomisakae.mc_mcp.api.service.ItemService;
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
    private final CommandController commandController;
    private final PlayerController playerController;
    private final WorldController worldController;
    private final ItemController itemController;

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
            
            // Bật log chi tiết cho development
            javalinConfig.plugins.enableDevLogging();
            
            // Cấu hình JSON mapper
            javalinConfig.jsonMapper(new JavalinJackson(objectMapper));
        }).exception(Exception.class, new ApiExceptionHandler());
        
        // Khởi tạo các service và controller
        InventoryService inventoryService = new InventoryService();
        PlayerService playerService = new PlayerService();
        WorldService worldService = new WorldService();
        ItemService itemService = new ItemService();
        
        this.inventoryController = new InventoryController(inventoryService);
        this.commandController = new CommandController();
        this.playerController = new PlayerController(playerService);
        this.worldController = new WorldController(worldService);
        this.itemController = new ItemController(itemService);
        
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

    // Phương thức này không cần thiết vì cấu hình đã được thực hiện trong constructor

    /**
     * Đăng ký các route cho API
     */
    private void registerRoutes() {
        // Đăng ký route cho health check
        app.get("/api/health", this::healthCheck);
        
        // Đăng ký route cho thực thi lệnh - sử dụng POST để gửi dữ liệu trong body
        app.post("/api/command", commandController::executeCommand);
        
        // Đăng ký các route cho thông tin người chơi - tất cả đều sử dụng POST với tên người chơi trong body
        app.post("/api/player/info", playerController::getPlayerInfo);
        app.post("/api/player/inventory", inventoryController::getPlayerInventoryAsJson);
        app.post("/api/player/surroundings", playerController::getPlayerSurroundings);
        app.post("/api/player/entities", playerController::getNearbyEntities);
        app.post("/api/player/statistics", playerController::getPlayerStatistics);
        
        // Đăng ký route cho thông tin thế giới
        app.get("/api/world/info", worldController::getWorldInfo);
        
        // Đăng ký route cho vật phẩm và công thức chế tạo
        app.get("/api/items", itemController::getAllItems);
        app.post("/api/items/recipes", itemController::getRecipesForItem);
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
