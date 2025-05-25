package com.tomisakae.mc_mcp.api;

/**
 * Lớp đại diện cho cấu trúc phản hồi API chuẩn
 * @param <T> Kiểu dữ liệu của dữ liệu phản hồi
 */
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
