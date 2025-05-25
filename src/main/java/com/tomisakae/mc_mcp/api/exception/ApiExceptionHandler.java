package com.tomisakae.mc_mcp.api.exception;

import com.tomisakae.mc_mcp.Mcmcpmod;
import com.tomisakae.mc_mcp.api.ApiResponse;
import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;

/**
 * Lớp xử lý các ngoại lệ trong API
 */
public class ApiExceptionHandler implements ExceptionHandler<Exception> {
    
    @Override
    public void handle(Exception exception, Context ctx) {
        Mcmcpmod.LOGGER.error("API error: " + exception.getMessage(), exception);
        
        ApiResponse<Object> errorResponse = new ApiResponse<>(
            false,
            "Lỗi máy chủ: " + exception.getMessage(),
            null
        );
        
        ctx.status(500);
        ctx.json(errorResponse);
    }
}
