package com.tomisakae.mc_mcp.api.config;

/**
 * Lớp quản lý cấu hình cho API server
 */
public class ApiConfig {
    private static final int DEFAULT_PORT = 7070;
    
    private int port;
    private boolean enableCors;
    private boolean enableSwagger;
    
    public ApiConfig() {
        this.port = DEFAULT_PORT;
        this.enableCors = true;
        this.enableSwagger = true;
    }
    
    public static ApiConfig defaultConfig() {
        return new ApiConfig();
    }
    
    public int getPort() {
        return port;
    }
    
    public ApiConfig setPort(int port) {
        this.port = port;
        return this;
    }
    
    public boolean isEnableCors() {
        return enableCors;
    }
    
    public ApiConfig setEnableCors(boolean enableCors) {
        this.enableCors = enableCors;
        return this;
    }
    
    public boolean isEnableSwagger() {
        return enableSwagger;
    }
    
    public ApiConfig setEnableSwagger(boolean enableSwagger) {
        this.enableSwagger = enableSwagger;
        return this;
    }
}
