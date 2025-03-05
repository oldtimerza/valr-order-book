package com.valr.orderbook.infrastructure.api.response;

public class ErrorResponse {
    private String message;

    private ErrorCode code;

    public ErrorResponse(String message, ErrorCode code) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public ErrorCode getCode() {
        return code;
    }
}
