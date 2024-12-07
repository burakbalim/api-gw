package com.application.gateway.common.exception.model;

public class ApiResponse<T> {
    private T data;
    private ApiResult result;

    public ApiResponse(T data, ApiResult result) {
        this.data = data;
        this.result = result;
    }

    public ApiResponse(ApiResult result) {
        this.result = result;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ApiResult getResult() {
        return this.result;
    }

    public void setResult(ApiResult result) {
        this.result = result;
    }
}
