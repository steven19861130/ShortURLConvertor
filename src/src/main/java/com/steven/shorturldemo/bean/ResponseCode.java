package com.steven.shorturldemo.bean;

public enum ResponseCode {

    OK("SUCCESS"),
    FAIL("FAIL");

    private String responseCode;

    ResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String toResponse() {
        return responseCode;
    }
}
